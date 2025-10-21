package poo.model;

public class Usuario {
    private Long id;
    private String username;
    private String nome;
    
    public Usuario(Long id, String username, String nome) {
        this.id = id;
        this.username = username;
        this.nome = nome;
    }

    public Usuario() {}

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getNome() {
        return nome;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
