package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import runner.AndorinhaTestRunner;

@RunWith(AndorinhaTestRunner.class)
public class TestTweetRepository {
	
	private TweetRepository tweetRepository;
	private UsuarioRepository usuarioRepository;
	
	@Before
	public void setUp() {
		this.tweetRepository = new TweetRepository();
		this.usuarioRepository = new UsuarioRepository();
	}
	
	@Test
	public void testa_se_tweet_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		int idConsulta = 4;
		Usuario user = this.usuarioRepository.consultar(idConsulta);
		
		Tweet tweet = new Tweet();
		tweet.setConteudo("Tweet do Teste de Unidade");
		tweet.setUsuario(user);
		this.tweetRepository.inserir(tweet);
		
		Tweet inserido = this.tweetRepository.consultar(tweet.getId());
		Usuario userInserido = this.usuarioRepository.consultar(inserido.getUsuario().getId());
		inserido.setUsuario(userInserido);
		
		assertThat(tweet.getId()).isGreaterThan(0);

		assertThat(inserido.getId()).isEqualTo(tweet.getId());
		assertThat(inserido.getConteudo()).isEqualTo(tweet.getConteudo());
		assertThat(inserido.getUsuario()).isNotNull();
		assertThat(inserido.getUsuario().getId()).isEqualTo(user.getId());
	}
	
//	@Test
	public void testa_se_tweet_foi_alterado() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = new Tweet();
		tweet.setId(12);
		tweet.setConteudo("Tweet do Teste de Unidade Alterado");
		this.tweetRepository.atualizar(tweet);
		
		Tweet inserido = this.tweetRepository.consultar(tweet.getId());
		
		assertThat(tweet.getId()).isGreaterThan(0);
		
		assertThat(inserido.getConteudo()).isEqualTo(tweet.getConteudo());
		assertThat(inserido.getId()).isEqualTo(tweet.getId());
	}
	
//	@Test
	public void testa_se_tweet_foi_removido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = new Tweet();
		tweet.setId(14);
		tweet.setConteudo("Tweet do Teste de Unidade");
		this.tweetRepository.remover(tweet);
		
		assertThat(this.tweetRepository.consultar(tweet.getId())).isNull();
	}
	
//	@Test
	public void testa_consultar_tweet() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		int idConsulta = 13;
		Tweet tweet = this.tweetRepository.consultar(idConsulta);
		Usuario userInserido = this.usuarioRepository.consultar(tweet.getUsuario().getId());
		tweet.setUsuario(userInserido);
		
		assertThat(tweet).isNotNull();
		assertThat(tweet.getConteudo()).isEqualTo("Tweet do Teste de Unidade");
		assertThat(tweet.getId()).isEqualTo(idConsulta);
	}
	
//	@Test
	public void testa_listar_todos_tweets() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		List<Tweet> tweets =  this.tweetRepository.listarTodos();
		
		assertThat(tweets).isNotNull();
	}

}
