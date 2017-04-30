package mdome7.graphite.util;

import mdome7.graphite.model.RawData;
import mdome7.graphite.ParsingException;

import java.io.*;
import java.util.*;

/**
 * Parses the raw data response.
 * Most efficient in terms of memory.
 *
 * NOT COMPLETELY IMPLEMENTED
 */
public class RawDataParser {

    private enum Mode { PARSING_METADATA, PARSING_DATA }

    public final static String NO_DATA_TERM = "None";

    private static class CurrentState {
        private Mode mode;
        private int startPtr;
        private int endPtr;
        private RawData rawData;

        private CurrentState(Mode mode, int startPtr) {
            this.mode = mode;
            this.startPtr = startPtr;
            this.endPtr = startPtr;
            this.rawData = null;
        }
    }

    @Deprecated
    public List<RawData> parseLine(InputStream is) throws IOException, ParsingException {
        List<RawData> rawDataList = new ArrayList<>();
        char [] cbuff = new char[2048];
        boolean parsingMetadata = true;
        CurrentState state = new CurrentState(Mode.PARSING_METADATA, 0);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int len = br.read(cbuff, state.startPtr, cbuff.length - state.startPtr);
            if (len > 0) {
                state.endPtr = state.endPtr + len;
                if (state.mode == Mode.PARSING_METADATA) {
                    if (startNewRawData(cbuff, state)) {
                        rawDataList.add(state.rawData);
                    }
                }
                if (state.mode == Mode.PARSING_DATA) {

                }

            } else if (parsingMetadata) {
                throw new ParsingException("End of input reached while still parsing metadata");
            }
        }

        return rawDataList;
    }

    /**
     * Returns a new RawData if is able to parse metadata based on what's currently in the buffer.
     *
     * @param cbuff
     * @param state
     * @return
     * @throws ParsingException
     */
    @Deprecated
    private boolean startNewRawData(char [] cbuff, CurrentState state) throws ParsingException {
        if (state.mode != Mode.PARSING_METADATA) throw new ParsingException("Invalid state - expecting " + Mode.PARSING_METADATA + " but found " + state.mode);
        state.rawData = null;

        int n = 0;
        RawData rawData = new RawData();

        boolean numeric = true;
        int pipeIndex = -1;
        int lastCommaIndex = state.startPtr;
        for (int i = state.startPtr; i <= state.endPtr; i++) {
            char c = cbuff[i];
            if (c == ',') {
                if (numeric) {
                    switch (n) {
                        case 0:
                            rawData.setStartTimestamp(parseLong(cbuff, lastCommaIndex, i));
                            break;
                        case 1:
                            rawData.setEndTimestamp(parseLong(cbuff, lastCommaIndex, i));
                            break;
                        default:
                            throw new ParsingException("Found more then 3 numeric values while parsing metadata: " +
                                    new String(cbuff).substring(state.startPtr, i + 1));
                    }
                    n++;
                } else {
                    String name = new String(cbuff).substring(lastCommaIndex, i + 1);
                    rawData.setName( rawData.getName() != null ? rawData.getName() + "," + name : name);
                }
                numeric = true;
                lastCommaIndex = i;
            } else if (c == '|') {
                if (numeric && n == 2) {
                    rawData.setStep((int) parseLong(cbuff, lastCommaIndex, i));
                } else {
                    throw new ParsingException("Found more then 3 numeric values while parsing metadata: " +
                            new String(cbuff).substring(state.startPtr, i + 1));
                }
                pipeIndex = i;
            } else if (!isDigit(c)) {
                numeric = false;
            }
        }
        if (pipeIndex < 0) { // we did not find all the values
            shiftLeft(cbuff, state);
            return false;
        } else {
            state.startPtr = pipeIndex + 1;
            state.mode = Mode.PARSING_DATA;
            state.rawData = rawData;
            return true;
        }
    }

    /**
     * Parses a single line from the raw response to a RawData object.
     * @param line
     * @return
     */
    public static RawData parseLine(String line) throws ParsingException {
        String [] tokens = line.split("\\|");
        RawData rawData = parseMetadata(tokens[0]);
        parseValues(rawData, tokens[1]);
        return rawData;
    }

    public static RawData parseMetadata(String metadata) throws ParsingException {
        char [] cbuff = metadata.toCharArray();

        RawData rawData = new RawData();
        int n = 0;
        int lastCommaIndex = cbuff.length;
        outerloop:
        for (int i = cbuff.length - 1; i >= 0; i--) {
            if (cbuff[i] == ',') {
                switch (n) {
                    case 0:
                        rawData.setStep((int) parseLong(cbuff, i + 1, lastCommaIndex - 1));
                        n++;
                        break;
                    case 1:
                        rawData.setEndTimestamp(parseLong(cbuff, i + 1, lastCommaIndex - 1));
                        n++;
                        break;
                    default:
                        rawData.setStartTimestamp(parseLong(cbuff, i + 1, lastCommaIndex - 1));
                        rawData.setName(metadata.substring(0, i));
                        return rawData;
                }
                lastCommaIndex = i;
            }
        }
        if (rawData.getName() == null) throw new ParsingException("Unable to parse metadata from string \"" + metadata + "\"");
        return rawData;
    }

    public static void parseValues(RawData rawData, String values) {
        StringTokenizer st = new StringTokenizer(values, ",");
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            rawData.addValue(NO_DATA_TERM.equals(token) ? null : new Double(token));
        }
    }

    public static long parseLong(char [] cbuff) throws ParsingException {
        return parseLong(cbuff, 0, cbuff.length - 1);
    }

    /**
     *
     * @param cbuff
     * @param start index inclusive
     * @param end index inclusive
     * @return
     * @throws ParsingException
     */
    public static long parseLong(char [] cbuff, int start, int end) throws ParsingException {
        long m = 1;
        long value = 0L;
        for (int i = end; i >= start; i--) {
            value += m * parseDigit(cbuff[i]);
            m *= 10;
        }
        return value;
    }

    public static int parseDigit(char digit) throws ParsingException {
        if (!isDigit(digit)) {
            throw new ParsingException("Character " + digit + " is not a digit");
        }
        return ((int) digit) - 48;
    }

    public static boolean isDigit(char candidate) {
        return '0' <= candidate && candidate <= '9';
    }

    private void shiftLeft(char [] cbuff, CurrentState state) {
        int len = state.endPtr - state.startPtr;
        // shift all the data to the start of the buffer and read more from input stream
        for (int i = state.startPtr; i < len; i++) {
            cbuff[i - state.startPtr] = cbuff[i];
        }
        state.startPtr = 0;
        state.endPtr = len  - 1;
    }
}
