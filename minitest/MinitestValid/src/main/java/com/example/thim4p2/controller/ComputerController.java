package com.example.thim4p2.controller;

import com.example.thim4p2.configuration.DuplicateProductNameException;
import com.example.thim4p2.dto.ComputerDTO;
import com.example.thim4p2.model.Computer;
import com.example.thim4p2.model.TypeComputer;
import com.example.thim4p2.service.IComputerService;
import com.example.thim4p2.service.ITypeComputerService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/")
public class ComputerController {
    @Autowired
    IComputerService computerService;
    @Autowired
    ITypeComputerService typeComputerService;
    @GetMapping("/")
    public String homeProduct(Model model, @RequestParam(required = false, defaultValue = "") String search,
                              @RequestParam(required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page,2);
        Page<Computer> computers = computerService.search(search, pageable);
        model.addAttribute("search", search);
        model.addAttribute("computers", computers);
        return "home";
    }
    @GetMapping("/xoa")
    public String deleteProduct(@RequestParam("id") int id, RedirectAttributes redirectAttributes, Model model) {
        computerService.deleteComputer(id);
        redirectAttributes.addFlashAttribute("msg", 3);
        return "redirect:/";
    }
    @GetMapping("/add")
    public String showFormAddProduct(Model model) {
        ComputerDTO computerDTO = new ComputerDTO();
        List<TypeComputer> typeComputer = typeComputerService.listTypeComputer();
        model.addAttribute("computerDTO", computerDTO);
        model.addAttribute("typeComputer", typeComputer);
        return "/add";
    }
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("computerDTO") ComputerDTO computerDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasFieldErrors()) {
            List<TypeComputer> typeComputer = typeComputerService.listTypeComputer();
            model.addAttribute("typeComputer", typeComputer);
            return "/add";
        } else {
            if (computerService.isComputerNameExists(computerDTO.getCodeComputer())) {
                throw new DuplicateProductNameException("Mã máy tính '" + computerDTO.getCodeComputer() + "' đã tồn tại");
            }
            Computer computer = new Computer();
            BeanUtils.copyProperties(computerDTO, computer);
            computerService.addComputer(computer);
            redirectAttributes.addFlashAttribute("msg", 1);
            return "redirect:/";
        }

    }
    @GetMapping("/edit/{id}")
    public String showFormEdit(Model model, @PathVariable int id) {
        Computer computer = new Computer();
        ComputerDTO computerDTO = new ComputerDTO();
        computer = computerService.info(id);
        BeanUtils.copyProperties(computer, computerDTO);
        List<TypeComputer> typeComputers = typeComputerService.listTypeComputer();
        model.addAttribute("computerDTO", computerDTO);
        model.addAttribute("typeComputers", typeComputers);
        return "/edit";
    }
    @PostMapping("edit")
    public String editProduct(@Valid @ModelAttribute("computerDTO") ComputerDTO computerDTO,BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes){
        if (bindingResult.hasFieldErrors()) {
            List<TypeComputer> typeComputers = typeComputerService.listTypeComputer();
            model.addAttribute("typeComputers", typeComputers);
            return "/edit";
        } else {
            Computer computer = new Computer();
            BeanUtils.copyProperties(computerDTO, computer);
            computerService.addComputer(computer);
            redirectAttributes.addFlashAttribute("msg", 2);
            return "redirect:/";
        }
    }
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoSuchElementException(NoSuchElementException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, Model model) {
        model.addAttribute("errorMessage", "Sai định dạng ID : " + e.getValue());
        return "error/400";
    }
   @ExceptionHandler(DuplicateProductNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDuplicateProductNameException(DuplicateProductNameException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/duplicateProductName";
    }
}
