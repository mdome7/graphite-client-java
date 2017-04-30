package mdome7.graphite;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import mdome7.graphite.convert.DataPointJSONDeserializer;
import mdome7.graphite.model.DataPoint;
import mdome7.graphite.model.DataSeries;
import mdome7.graphite.model.RawData;
import mdome7.graphite.model.RenderRequest;
import mdome7.graphite.util.RawDataParser;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of client.
 */
public class RenderClientImpl implements RenderClient {

    private String baseUrl;

    private WebTarget target;

    public RenderClientImpl(String baseUrl) {
        this.baseUrl = baseUrl;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(DataPoint.class, new DataPointJSONDeserializer());
        mapper.registerModule(customModule);
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(mapper);

        Client client = ClientBuilder.newClient(new ClientConfig(jacksonProvider));
        target = client.target(baseUrl);
    }

    public List<DataSeries> fetchDataSeries(RenderRequest request) throws APIException {
        WebTarget wt = prepareTarget(request, "json");

        Response response = wt.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return response.readEntity(new GenericType<List<DataSeries>>() {});
        } else {
            throw new APIException(response.getStatus(), "Failed to get fetchDataSeries data");
        }
    }

    @Override
    public List<RawData> fetchRawData(RenderRequest request) throws APIException {
        WebTarget wt = prepareTarget(request, "raw");

        String line = null;
        Response response = wt.request().accept(MediaType.TEXT_PLAIN_TYPE).get();
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)), 2048)) {
                List<RawData> list = new ArrayList<>();
                while((line = br.readLine()) != null) {
                    list.add(RawDataParser.parseLine(line));
                }
                return list;
            } catch (IOException e) {
                throw new APIException("Error fetching raw data", e);
            } catch (ParsingException e) {
                throw new APIException("Unable to parse raw data response - line: " + line, e);
            }
        } else {
            throw new APIException(response.getStatus(), "Failed to get fetchDataSeries data");
        }
    }



    private WebTarget prepareTarget(RenderRequest request, String format) {
        // TODO: do request validation
        WebTarget wt = target.path("render");
        wt = wt.queryParam("format", format)
                .queryParam("from", request.getFrom().toParamString());
        for (String t : request.getTargets()) {
            wt = wt.queryParam("target", t);
        }
        if (request.getUntil() != null) {
            wt = wt.queryParam("until", request.getFrom().toParamString());
        }
        return wt;
    }
}
