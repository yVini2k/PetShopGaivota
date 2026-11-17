package com.petshopgaivota.api.entity;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "contatos")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
            regexp = "\\d{10,11}",
            message = "O telefone deve conter 10 ou 11 números"
    )
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(
            min = 5,
            max = 200,
            message = "O endereço deve ter entre 5 e 200 caracteres"
    )
    private String endereco;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}