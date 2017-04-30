package mdome7.graphite.util;

import mdome7.graphite.model.RawData;
import static org.junit.Assert.*;

import mdome7.graphite.ParsingException;
import org.junit.Test;

import java.util.List;

public class RawDataParserTest {

    @Test
    public void testParseLine() throws ParsingException {
        String rawResponse = "movingAverage(My_Series,10),1493017095,1493018895,15|None,1.0,2.0,3.0,None";
        RawData rawData = RawDataParser.parseLine(rawResponse);
        assertEquals("movingAverage(My_Series,10)", rawData.getName());
        assertEquals(1493017095L, rawData.getStartTimestamp());
        assertEquals(1493018895, rawData.getEndTimestamp());
        assertEquals(15, rawData.getStep());

        List<Double> data = rawData.getValues();
        assertEquals(5, data.size());
        assertNull(data.get(0));
        assertEquals(1.0D, data.get(1), 0.001);
        assertEquals(2.0D, data.get(2), 0.001);
        assertEquals(3.0D, data.get(3), 0.001);
        assertNull(data.get(4));
    }

    @Test
    public void testParseRawDataMetadata() throws ParsingException {
        RawData rawData = RawDataParser.parseMetadata("movingAverage(My_Series,10),1493017095,1493018895,15");
        assertEquals("movingAverage(My_Series,10)", rawData.getName());
        assertEquals(1493017095L, rawData.getStartTimestamp());
        assertEquals(1493018895, rawData.getEndTimestamp());
        assertEquals(15, rawData.getStep());
    }

    @Test
    public void testParseLong() throws ParsingException {
        String sValue = "12345";
        long lValue = RawDataParser.parseLong(sValue.toCharArray());
        assertEquals(12345L, lValue);
    }

    @Test
    public void testIsDigit() throws ParsingException {
        assertTrue(RawDataParser.isDigit('9'));
        assertTrue(RawDataParser.isDigit('5'));
        assertTrue(RawDataParser.isDigit('2'));
        assertTrue(RawDataParser.isDigit('0'));
        assertFalse(RawDataParser.isDigit('a'));
        assertFalse(RawDataParser.isDigit('Z'));
        assertFalse(RawDataParser.isDigit('\0'));
        assertFalse(RawDataParser.isDigit('\n'));
        assertFalse(RawDataParser.isDigit('\uFF21'));
    }

    @Test
    public void testParseDigit() throws ParsingException {
        assertEquals(8, RawDataParser.parseDigit('8'));
        assertEquals(4, RawDataParser.parseDigit('4'));
        assertEquals(0, RawDataParser.parseDigit('0'));
    }

    @Test(expected = ParsingException.class)
    public void testParseNonDigit() throws ParsingException {
        RawDataParser.parseDigit('a');
    }
}
