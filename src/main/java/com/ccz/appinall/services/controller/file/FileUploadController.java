package com.ccz.appinall.services.controller.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {
	private static String UPLOADED_FOLDER = "./";
    @RequestMapping(value = "/fileUpload", method = RequestMethod.GET)
    public String displayForm() {

        return "upload";
    }
    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") final MultipartFile file, final ModelMap modelMap) {
        modelMap.addAttribute("file", file);
        return "/";
    }

    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.POST)
    public String submit(@RequestParam("files") final MultipartFile[] files, final ModelMap modelMap) {

        modelMap.addAttribute("files", files);
        return "fileUploadView";
    }

    @RequestMapping(value = "/uploadFileWithAddtionalData", method = RequestMethod.POST)
    public String submit(@RequestParam final MultipartFile file, @RequestParam final String name, @RequestParam final String email, final ModelMap modelMap) {

        modelMap.addAttribute("name", name);
        modelMap.addAttribute("email", email);
        modelMap.addAttribute("file", file);
        return "fileUploadView";
    }

    @RequestMapping(value = "/uploadFileModelAttribute", method = RequestMethod.POST)
    public String submit(@ModelAttribute final FormDataWithFile formDataWithFile, final ModelMap modelMap) {

        modelMap.addAttribute("formDataWithFile", formDataWithFile);
        return "fileUploadView";
    }
}
