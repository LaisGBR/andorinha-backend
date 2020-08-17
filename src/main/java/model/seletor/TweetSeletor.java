package model.seletor;

import java.util.Calendar;

public class TweetSeletor  extends AbstractBaseSeletor{


	private Integer id;
	private String conteudo;
	private Calendar data;
	private Integer idUsuario;
	
	public boolean possuiFiltro() {
		return this.id != null || 
			  (this.conteudo != null && !this.conteudo.trim().isEmpty() ) ||
			  this.data != null  || this.idUsuario != null 
				;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	

}
