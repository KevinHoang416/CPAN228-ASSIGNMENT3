package com.cpan228.Assignment1.controller;

import java.util.EnumSet;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cpan228.Assignment1.model.Item;
import com.cpan228.Assignment1.model.Item.Brand;
import com.cpan228.Assignment1.model.User;
import com.cpan228.Assignment1.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/creation")
public class creationController {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public String creation() {
        return "creation";
    }

    @ModelAttribute
    public void brands(Model model) {
        var brands = EnumSet.allOf(Brand.class);
        model.addAttribute("brands", brands);
        log.info("brands converted", brands);
    }

    @ModelAttribute
    public Item item() {
        return Item
                .builder()
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WAREHOUSE_EMPLOYEE')")
    public String processItemAddition(@Valid Item item, BindingResult result) {
        if (result.hasErrors()) {
            return "creation";
        }
        log.info("Processing item: {}", item);
        itemRepository.save(item);
        return "redirect:/itemlist";
    }

    @PostMapping("/deleteItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processItemDeletion(Long itemId) {
        log.info("Deleting item with id: {}", itemId);
        itemRepository.deleteById(itemId);
        return "redirect:/itemlist";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processFightersDeletion(@AuthenticationPrincipal User user) {
        log.info("Deleting all fighters for user: {}", user.getAuthorities());
        itemRepository.deleteAll();
        return "redirect:/creation";
    }

}
