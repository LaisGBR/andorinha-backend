package service;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import model.Comentario;
import model.Usuario;
import model.dto.ComentarioDTO;
import model.seletor.ComentarioSeletor;
import repository.ComentarioRepository;

@Path("/comentario")
public class ComentarioService {

	@EJB
	ComentarioRepository comentarioRepository;
	
	@Context
	private SecurityContext context;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> listarTodos(){
		return this.comentarioRepository.listarTodos();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public int inserir(Comentario comentario) {
		comentario.setUsuario( (Usuario)this.context.getUserPrincipal() );
		this.comentarioRepository.inserir(comentario);
		return comentario.getId();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public void atualizar(Comentario comentario) {
		comentario.setUsuario( (Usuario)this.context.getUserPrincipal() );
		this.comentarioRepository.atualizar(comentario);
	}
	
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Comentario consultar( @PathParam("id") Integer id) {
		return this.comentarioRepository.consultar(id);
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remover( @PathParam("id") Integer id) {
		this.comentarioRepository.remover(id);
	}
	
	
	@POST
	@Path("/pesquisar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comentario> pesquisar( ComentarioSeletor seletor ){
		return this.comentarioRepository.pesquisar(seletor);
	}
	
	@POST
	@Path("/dto")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<ComentarioDTO> pesquisarDTO( ComentarioSeletor seletor ){
		return this.comentarioRepository.pesquisarDTO(seletor);
	}
	
	@POST
	@Path("/contar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Long contar( ComentarioSeletor seletor ){
		return this.comentarioRepository.contar(seletor);
	}
}
