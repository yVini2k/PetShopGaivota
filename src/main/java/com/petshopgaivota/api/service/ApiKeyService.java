package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.ApiKey;
import com.petshopgaivota.api.enums.NivelAcesso;
import com.petshopgaivota.api.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repo;

    public ApiKeyService(ApiKeyRepository repo) {
        this.repo = repo;
    }

    public String gerar(String proprietario, NivelAcesso nivel) {
        String chave = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        ApiKey k = new ApiKey();
        k.setProprietario(proprietario);
        k.setNivel(nivel);
        k.setChaveHash(sha256(chave));
        repo.save(k);
        return chave;
    }

    /**
     * Garante que a chave fixa (vinda de variável de ambiente) exista no banco, ativa e como ADMIN.
     * Idempotente: pode ser chamada a cada inicialização, mesmo com o banco zerado.
     */
    public void garantirChaveFixa(String chave, String proprietario) {
        String hash = sha256(chave);
        ApiKey k = repo.findByChaveHash(hash).orElseGet(ApiKey::new);
        k.setChaveHash(hash);
        k.setProprietario(proprietario);
        k.setNivel(NivelAcesso.ADMIN);
        k.setAtiva(true);
        repo.save(k);
    }

    public Optional<ApiKey> validar(String chave) {
        return repo.findByChaveHashAndAtivaTrue(sha256(chave));
    }

    public List<ApiKey> listar() {
        return repo.findAll();
    }

    public boolean revogar(Long id) {
        Optional<ApiKey> k = repo.findById(id);
        if (k.isEmpty()) {
            return false;
        }
        k.get().setAtiva(false);
        repo.save(k.get());
        return true;
    }

    private static String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256")
                    .digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}