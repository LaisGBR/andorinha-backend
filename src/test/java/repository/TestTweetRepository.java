package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import model.Tweet;
import model.Usuario;
import model.dto.TweetDTO;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.TweetSeletor;
import runner.AndorinhaTestRunner;
import runner.DatabaseHelper;

@RunWith(AndorinhaTestRunner.class)
public class TestTweetRepository {

	private static final int ID_TWEET_CONSULTA = 1;
	private static final int ID_USUARIO_CONSULTA = 1;

	private static final long DELTA_MILIS = 500;

	@EJB
	private UsuarioRepository usuarioRepository;
	
	@EJB
	private TweetRepository tweetRepository;

	@Before
	public void setUp() {
		DatabaseHelper.getInstance("andorinhaDS").execute("dataset/andorinha.xml", DatabaseOperation.CLEAN_INSERT);
	}

	@Test
	public void testa_se_tweet_foi_inserido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Usuario user = this.usuarioRepository.consultar(ID_USUARIO_CONSULTA);

		Tweet tweet = new Tweet();
		tweet.setConteudo("Minha postagem de teste");
		tweet.setUsuario(user);

		this.tweetRepository.inserir(tweet);

		assertThat(tweet.getId()).isGreaterThan(0);

		Tweet inserido = this.tweetRepository.consultar(tweet.getId());

		assertThat(inserido).isNotNull();
		assertThat(inserido.getConteudo()).isEqualTo(tweet.getConteudo());
		assertThat(Calendar.getInstance().getTime()).isCloseTo(inserido.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_se_tweet_foi_alterado() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		tweet.setConteudo("Alterado!");

		this.tweetRepository.atualizar(tweet);

		Tweet alterado = this.tweetRepository.consultar(ID_TWEET_CONSULTA);

		assertThat(alterado.getConteudo()).isEqualTo(tweet.getConteudo());
		assertThat(Calendar.getInstance().getTime()).isCloseTo(alterado.getData().getTime(), DELTA_MILIS);
	}

	@Test
	public void testa_se_tweet_foi_removido() throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		assertThat(tweet).isNotNull();

		this.tweetRepository.remover(ID_TWEET_CONSULTA);

		Tweet removido = this.tweetRepository.consultar(ID_TWEET_CONSULTA);
		assertThat(removido).isNull();

		assertThat(this.tweetRepository.consultar(tweet.getId())).isNull();
	}

	@Test
	public void testa_consultar_tweet() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		Tweet tweet = this.tweetRepository.consultar(ID_TWEET_CONSULTA);

		assertThat(tweet).isNotNull();
		assertThat(tweet.getConteudo()).isEqualTo("Minha postagem de teste");
		assertThat(tweet.getId()).isEqualTo(ID_TWEET_CONSULTA);
		assertThat(tweet.getUsuario()).isNotNull();
	}

	@Test
	public void testa_listar_todos_tweets()  {
		List<Tweet> tweets = this.tweetRepository.listarTodos();

		assertThat(tweets).isNotNull().isNotEmpty().hasSize(6).extracting("conteudo").containsExactlyInAnyOrder(
				"Minha postagem de teste", "Minha postagem de teste 2", "Minha postagem de teste 3", "Tweet 4", "Tweet 5", "Tweet 6");

		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}
	
	@Test
	public void testa_pesquisar_os_tweets_por_conteudo() {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setConteudo("Minha postagem de teste 2");
		
		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste 2");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}
	
	@Test
	public void testa_pesquisar_os_tweets_por_data() throws ParseException {
		TweetSeletor seletor = new TweetSeletor();
		String data = "2020-01-01 12:55:15";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(data));
		seletor.setData(cal);
		
		
		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(4)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste 3","Tweet 4", "Tweet 5", "Tweet 6");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}
	

	@Test
	public void testa_pesquisar_os_tweets_por_idUsuario()  {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setIdUsuario(1);
		
		List<Tweet> tweets = this.tweetRepository.pesquisar(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getUsuario()).isNotNull();
		});
	}
	
	@Test
	public void testa_contar_comentarios_por_id()  {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setId(1);
		Long total = this.tweetRepository.contar(seletor);
		
		assertThat( total ).isNotNull()
		.isEqualTo(1L);
	}
	
	
	@Test
	public void testa_pesquisarDTO_os_tweets_por_conteudo() {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setConteudo("Minha postagem de teste 2");
		
		List<TweetDTO> tweets = this.tweetRepository.pesquisarDTO(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste 2");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getIdUsuario()).isNotNull();
		});
	}
	
	@Test
	public void testa_pesquisarDTO_os_tweets_por_data() throws ParseException {
		TweetSeletor seletor = new TweetSeletor();
		String data = "2020-01-01 12:55:15";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(data));
		seletor.setData(cal);
		
		
		List<TweetDTO> tweets = this.tweetRepository.pesquisarDTO(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(4)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste 3","Tweet 4", "Tweet 5", "Tweet 6");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getIdUsuario()).isNotNull();
		});
	}
	

	@Test
	public void testa_pesquisarDTO_os_tweets_por_idUsuario()  {
		TweetSeletor seletor = new TweetSeletor();
		seletor.setIdUsuario(1);
		
		List<TweetDTO> tweets = this.tweetRepository.pesquisarDTO(seletor);
		
		assertThat( tweets ).isNotNull()
							.isNotEmpty()
							.hasSize(1)
							.extracting("conteudo")
							.containsExactlyInAnyOrder("Minha postagem de teste");
		
		tweets.stream().forEach(t -> {
			assertThat(t.getData()).isNotNull().isLessThan(Calendar.getInstance());
			assertThat(t.getIdUsuario()).isNotNull();
		});
	}

}
