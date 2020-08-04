package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TestUsuarioRepository {
	
	private UsuarioRepository usuarioRepository;
	
	@Before
	public void setUp() {
		this.usuarioRepository = new UsuarioRepository();
	}
	
	@Test
	public void testa_se_usuario_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = new Usuario();
		user.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.inserir(user);
		
		Usuario inserido = this.usuarioRepository.consultar(user.getId());
		
		assertThat(user.getId()).isGreaterThan(0);
		
		assertThat(inserido.getNome()).isEqualTo(user.getNome());
		assertThat(inserido.getId()).isEqualTo(user.getId());
	}
	
	@Test
	public void testa_se_usuario_foi_alterado() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = new Usuario();
		user.setId(3);
		user.setNome("Usuario do Teste de Unidade Alterado");
		this.usuarioRepository.atualizar(user);
		
		Usuario inserido = this.usuarioRepository.consultar(user.getId());
		
		assertThat(user.getId()).isGreaterThan(0);
		
		assertThat(inserido.getNome()).isEqualTo(user.getNome());
		assertThat(inserido.getId()).isEqualTo(user.getId());
	}
	
	@Test
	public void testa_se_usuario_foi_removido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = new Usuario();
		user.setId(6);
		user.setNome("Usuario do Teste de Unidade");
		this.usuarioRepository.remover(user);
		
		assertThat(this.usuarioRepository.consultar(user.getId())).isNull();
	}
	
	@Test
	public void testa_consultar_usuario() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		int idConsulta = 4;
		Usuario user = this.usuarioRepository.consultar(idConsulta);
		
		assertThat(user).isNotNull();
		assertThat(user.getNome()).isEqualTo("Usuario do Teste de Unidade");
		assertThat(user.getId()).isEqualTo(idConsulta);
	}
	
	@Test
	public void testa_listar_todos_usuarios() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		List<Usuario> users =  this.usuarioRepository.listarTodos();
		
		assertThat(users).isNotNull();
	}

}
