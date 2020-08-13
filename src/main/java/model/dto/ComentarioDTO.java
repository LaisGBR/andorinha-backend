package model.dto;

import java.util.Calendar;

public class ComentarioDTO {
	
	private int id;
	private String conteudo;
	private Calendar data;
	private int idUsuario;
	private int idTweet;
	
	public ComentarioDTO() {
		super();
	}

	public ComentarioDTO(int id, String conteudo, Calendar data, int idUsuario, int idTweet) {
		super();
		this.id = id;
		this.conteudo = conteudo;
		this.data = data;
		this.idUsuario = idUsuario;
		this.idTweet = idTweet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdTweet() {
		return idTweet;
	}

	public void setIdTweet(int idTweet) {
		this.idTweet = idTweet;
	}
	
}
