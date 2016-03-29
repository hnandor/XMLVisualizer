package com.nhuszka.web.spring.xml_visualizer.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nhuszka.web.spring.xml_visualizer.graph.GraphCreator;
import com.nhuszka.web.spring.xml_visualizer.graph.GraphInput;
import com.nhuszka.web.spring.xml_visualizer.model.FileUploadFormModel;
import com.nhuszka.web.spring.xml_visualizer.model.FilterFormModel;
import com.nhuszka.web.spring.xml_visualizer.model.StoredFilesModel;
import com.nhuszka.web.spring.xml_visualizer.storage.DiskFileStorage;
import com.nhuszka.web.spring.xml_visualizer.storage.StoredFileModel;
import com.nhuszka.web.spring.xml_visualizer.xml.XMLParser;

@Controller
public class XMLVisualizerController {

	private static final String UPLOAD_PAGE = "uploadfile";
	private static final String FILTER_PAGE = "filter";

	@RequestMapping(value = "/showUploadForm", method = RequestMethod.GET)
	public ModelAndView showUploadForm() {
		return new ModelAndView(UPLOAD_PAGE, "uploadForm", new FileUploadFormModel());
	}
	
	@RequestMapping(value = "/uploadFiles", method = RequestMethod.POST)
	public String saveFiles(HttpServletRequest request, @ModelAttribute("uploadForm") FileUploadFormModel uploadForm, Model model)
			throws IllegalStateException, IOException {
		List<StoredFileModel> storedFileModels = DiskFileStorage.getInstance().storeFiles(uploadForm.getFiles());
		
		request.getSession().setAttribute("storedFilesModel", createStoredFilesModel(storedFileModels));
		
		return FILTER_PAGE;
	}

	private StoredFilesModel createStoredFilesModel(List<StoredFileModel> fileModels) {
		StoredFilesModel storedFilesModel = new StoredFilesModel();
		for (StoredFileModel model : fileModels) {
			storedFilesModel.addStoredFile(model);
		}
		return storedFilesModel;
	}
	
	@RequestMapping(value = "/showGraph", method = RequestMethod.POST)
	public String createGraph(HttpServletRequest request, @ModelAttribute("filterForm") FilterFormModel filterFormModel, Model model)
			throws IllegalStateException, IOException {
		List<String> ids = filterFormModel.getIds();
		String beanPackageFilter = filterFormModel.getBeanPackageFilter();
		
		// TODO call the appropriate method with parameters
		GraphInput<String> graphInput = new XMLParser().createDemoGraphInput();
		String graphEncodedBase64 = new GraphCreator<String>().createGraphInBase64String(graphInput);
		
		model.addAttribute("graphBase64", graphEncodedBase64);
		model.addAttribute("beanPackageFilter", filterFormModel.getBeanPackageFilter());
		
		return FILTER_PAGE;
	}
}