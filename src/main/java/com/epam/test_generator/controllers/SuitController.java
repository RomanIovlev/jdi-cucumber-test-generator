package com.epam.test_generator.controllers;

import com.epam.test_generator.dto.SuitDTO;
import com.epam.test_generator.entities.Suit;
import com.epam.test_generator.services.SuitService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class SuitController {

    @Autowired
    SuitService suitService;

    @RequestMapping(value = "/")
    public String getMainPage() {
	@RequestMapping(value = "/")
    public String getMainPage() {
        return "/WEB-INF/static/views/newSuits";
    }

    @RequestMapping(value = "/suits", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<SuitDTO>> getSuits() {
		return new ResponseEntity<>(suitService.getSuits(), HttpStatus.OK);
    }

    @RequestMapping(value = "/suit/{suitId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SuitDTO> getSuit(@PathVariable("suitId") long id){
        SuitDTO suitDTO = suitService.getSuit(id);

		if (suitDTO != null) {
			return new ResponseEntity<>(suitDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value="/suit/{suitId}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<Void> editSuit( @PathVariable("suitId") long id, @RequestBody SuitDTO suitDTO){
        if (isNameValid(suitDTO.getName()) && isPriorityValid(suitDTO.getPriority()) &&
                isDescriptionValid(suitDTO.getDescription()) && isTagsValid(suitDTO.getTags())) {
            suitService.updateSuit(suitDTO);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @RequestMapping(value = "/suit/{suitId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeSuit(@PathVariable("suitId") long id){
        SuitDTO suitDTO = suitService.getSuit(id);

        if (suitDTO != null) {
            suitService.removeSuit(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value="/suit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<SuitDTO> addSuit(@RequestBody SuitDTO suitDTO) {
        if (isNameValid(suitDTO.getName()) && isPriorityValid(suitDTO.getPriority()) &&
                isDescriptionValid(suitDTO.getDescription()) && isTagsValid(suitDTO.getTags())) {
			return new ResponseEntity<>(suitService.addSuit(suitDTO), HttpStatus.OK);
        }

        return new ResponseEntity<>(suitDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private boolean isPriorityValid(Integer priority) {
        return priority != null && priority >= 1 && priority <= 5;
    }

    private boolean isNameValid(String name) {
        return name != null && name.length() >= 1 && name.length() <= 255;
    }

    private boolean isDescriptionValid(String description) {
        return description == null || description.length() <= 255;
    }

    private boolean isTagsValid(String tags) {
        return tags == null || tags.length() <= 255;
    }

}