// package: DTO de respuesta de Login
package co.edu.ue.dto;

public class RespuestaLogin {
    private String token;
    private String usuario;

    public RespuestaLogin() {}

    public RespuestaLogin(String token, String usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}