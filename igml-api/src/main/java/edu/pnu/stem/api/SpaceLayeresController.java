/**
 * 
 */
package edu.pnu.stem.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.pnu.stem.api.exception.UndefinedDocumentException;
import edu.pnu.stem.binder.IndoorGMLMap;
import edu.pnu.stem.dao.SpaceLayersDAO;
import edu.pnu.stem.feature.SpaceLayers;

/**
 * @author Hyung-Gyu Ryoo (hyunggyu.ryoo@gmail.com, Pusan National University)
 *
 */
@RestController
@RequestMapping("/documents/{docId}/spacelayers")
public class SpaceLayeresController {
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@PostMapping(value = "/{id}", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public void createSpaceLayeres(@PathVariable("docId") String docId,@PathVariable("id") String id, @RequestBody ObjectNode json, HttpServletRequest request, HttpServletResponse response) {

		String parentId = json.get("parentId").asText().trim();
		
		String name = null;
		String description = null;
		
		if(id == null || id.isEmpty()) {
			id = UUID.randomUUID().toString();
		}
		
		SpaceLayers sls;
		try {
			Container container = applicationContext.getBean(Container.class);
			IndoorGMLMap map = container.getDocument(docId);
			sls = SpaceLayersDAO.createSpaceLayers(map, parentId, id, name, description);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new UndefinedDocumentException();
		}
		response.setHeader("Location", request.getRequestURL().append(sls.getId()).toString());
	}
	@PutMapping(value = "/{id}", produces = "application/json")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateSpaceLayers(@PathVariable("docId") String docId,@PathVariable("id") String id, @RequestBody ObjectNode json, HttpServletRequest request, HttpServletResponse response) {
		try {
			Container container = applicationContext.getBean(Container.class);
			IndoorGMLMap map = container.getDocument(docId);

			String parentId = null;
			String name = null;
			String description = null;
			List<String> spaceLayerMember = null;
			
			if(json.has("parentId")) {
				parentId = json.get("parentId").asText().trim();
			}
						
			if(json.has("properties")){
				if(json.get("properties").has("name")) {
					name = json.get("properties").get("name").asText().trim();
				}
				if(json.get("properties").has("description")) {
					description = json.get("properties").get("description").asText().trim();
				}
				if(json.get("properties").has("spaceLayerMember")){
					spaceLayerMember = new ArrayList<String>();
					JsonNode partialBoundedByList = json.get("properties").get("spaceLayerMember");
					for(int i = 0 ; i < partialBoundedByList.size() ; i++){
						spaceLayerMember.add(partialBoundedByList.get(i).asText().trim());
					}
				}
				
			}
			
			SpaceLayersDAO.updateSpaceLayers(map, parentId, id, name, description, spaceLayerMember);
		}
		catch(NullPointerException e) {
			e.printStackTrace();
			throw new UndefinedDocumentException();
		}
	}
	
}
