package org.lab41.dendrite.web.controller.analysis;

import org.lab41.dendrite.models.GraphMetadata;
import org.lab41.dendrite.models.JobMetadata;
import org.lab41.dendrite.models.ProjectMetadata;
import org.lab41.dendrite.services.MetadataService;
import org.lab41.dendrite.services.MetadataTx;
import org.lab41.dendrite.services.analysis.EdgeDegreesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EdgeDegreesController {

    @Autowired
    MetadataService metadataService;

    @Autowired
    EdgeDegreesService edgeDegreesService;

    @RequestMapping(value = "/api/graphs/{graphId}/analysis/titan-degrees", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> startJob(@PathVariable String graphId) throws Exception {

        MetadataTx tx = metadataService.newTransaction();

        Map<String, Object> response = new HashMap<>();

        GraphMetadata graphMetadata = tx.getGraph(graphId);
        if (graphMetadata == null) {
            response.put("status", "error");
            response.put("msg", "missing graph metadata '" + graphId + "'");
            tx.rollback();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ProjectMetadata projectMetadata = graphMetadata.getProject();
        if (projectMetadata == null) {
            response.put("status", "error");
            response.put("msg", "missing project metadata for graph '" + graphId + "'");
            tx.rollback();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        JobMetadata jobMetadata = tx.createJob(projectMetadata);

        response.put("status", "ok");
        response.put("msg", "job submitted");
        response.put("jobId", jobMetadata.getId());

        tx.commit();

        // We can't pass the values directly because they'll live in a separate thread.
        edgeDegreesService.titanCountDegrees(graphMetadata.getId(), jobMetadata.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/graphs/{graphId}/analysis/faunus-degrees", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> startFaunusJob(@PathVariable String graphId) throws Exception {

        MetadataTx tx = metadataService.newTransaction();

        Map<String, Object> response = new HashMap<>();

        GraphMetadata graphMetadata = tx.getGraph(graphId);
        if (graphMetadata == null) {
            response.put("status", "error");
            response.put("msg", "missing graph metadata '" + graphId + "'");
            tx.rollback();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ProjectMetadata projectMetadata = graphMetadata.getProject();
        if (projectMetadata == null) {
            response.put("status", "error");
            response.put("msg", "missing project metadata for graph '" + graphId + "'");
            tx.rollback();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        JobMetadata jobMetadata = tx.createJob(projectMetadata);

        response.put("status", "ok");
        response.put("msg", "job submitted");
        response.put("jobId", jobMetadata.getId());

        tx.commit();

        // We can't pass the values directly because they'll live in a separate thread.
        edgeDegreesService.faunusCountDegrees(graphMetadata.getId(), jobMetadata.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}