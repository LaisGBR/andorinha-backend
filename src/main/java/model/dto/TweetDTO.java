package model.dto;

import java.util.Calendar;

public class TweetDTO {

	private int id;
	private String conteudo;
	private Calendar data;
	private int idUsuario;
	private String nomeUsuario;
	
	public TweetDTO() {
		super();
	}

	public TweetDTO(int id, String conteudo, Calendar data, int idUsuario, String nomeUsuario) {
		super();
		this.id = id;
		this.conteudo = conteudo;
		this.data = data;
		this.idUsuario = idUsuario;
		this.nomeUsuario = nomeUsuario;
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

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	
}
