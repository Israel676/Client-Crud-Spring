package com.example.clientcrud.controller;

import com.example.clientcrud.model.Client;
import com.example.clientcrud.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // List all clients
    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "index";
    }

    // Show form to create a new client
    @GetMapping("/new")
    public String showCreateForm(Client client) {
        return "create_client";
    }

    // Handle creation of a new client
    @PostMapping
    public String createClient(@Valid Client client, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "create_client";
        }

        // Check if CPF already exists
        if (clientRepository.existsById(client.getCpf())) {
            result.rejectValue("cpf", "error.client", "CPF already exists");
            return "create_client";
        }

        clientRepository.save(client);
        return "redirect:/clients";
    }

    // Show form to edit an existing client
    @GetMapping("/edit/{cpf}")
    public String showEditForm(@PathVariable("cpf") String cpf, Model model) {
        Client client = clientRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client CPF:" + cpf));
        model.addAttribute("client", client);
        return "edit_client";
    }

    // Handle update of an existing client
    @PostMapping("/update/{cpf}")
    public String updateClient(@PathVariable("cpf") String cpf, @Valid Client client, BindingResult result, Model model) {
        if (result.hasErrors()) {
            client.setCpf(cpf);
            return "edit_client";
        }

        clientRepository.save(client);
        return "redirect:/clients";
    }

    // Handle deletion of a client
    @GetMapping("/delete/{cpf}")
    public String deleteClient(@PathVariable("cpf") String cpf, Model model) {
        Client client = clientRepository.findById(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client CPF:" + cpf));
        clientRepository.delete(client);
        return "redirect:/clients";
    }
}
