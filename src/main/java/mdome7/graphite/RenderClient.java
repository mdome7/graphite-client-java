package mdome7.graphite;

import mdome7.graphite.model.DataSeries;
import mdome7.graphite.model.RawData;
import mdome7.graphite.model.RenderRequest;

import java.util.List;

/**
 * Client interface for the Graphite render API.
 */
public interface RenderClient {

    List<DataSeries> fetchDataSeries(RenderRequest request) throws APIException;

    List<RawData> fetchRawData(RenderRequest request) throws APIException;
}
