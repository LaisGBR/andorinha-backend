package repository;

import java.sql.Connection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.ComentarioSeletor;
import model.seletor.TweetSeletor;

@Stateless
public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			int id = this.recuperarProximoValorDaSequence("seq_tweet");
			tweet.setId(id);

			Calendar calendar = Calendar.getInstance();

			PreparedStatement ps = c
					.prepareStatement("insert into tweet (id, conteudo, data_postagem, id_usuario) values (?,?,?,?)");
			ps.setInt(1, tweet.getId());
			ps.setString(2, tweet.getConteudo());
			ps.setTimestamp(3, new Timestamp(calendar.getTimeInMillis()));
			ps.setInt(4, tweet.getUsuario().getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o tweet", e);
		}
	}

	public void atualizar(Tweet tweet) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Calendar hoje = Calendar.getInstance();

			PreparedStatement ps = c.prepareStatement("update tweet set conteudo = ?, data_postagem = ? where id = ?");
			ps.setString(1, tweet.getConteudo());
			ps.setTimestamp(2, new Timestamp(hoje.getTimeInMillis()));
			ps.setInt(3, tweet.getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o tweet", e);
		}
	}

	public void remover(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {

			PreparedStatement ps = c.prepareStatement("delete from tweet where id = ?");
			ps.setInt(1, id);
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o tweet", e);
		}

	}

	public Tweet consultar(int id) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Tweet tweet = null;

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT t.id, t.conteudo, t.data_postagem, t.id_usuario, u.nome as nome_usuario FROM tweet t ");
			sql.append("JOIN usuario u on t.id_usuario = u.id ");
			sql.append("WHERE t.id = ? ");

			PreparedStatement ps = c.prepareStatement(sql.toString());
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				tweet = new Tweet();
				tweet.setId(rs.getInt("id"));
				tweet.setConteudo(rs.getString("conteudo"));

				Calendar data = new GregorianCalendar();
				data.setTime(rs.getTimestamp("data_postagem"));
				tweet.setData(data);

				Usuario user = new Usuario();
				user.setId(rs.getInt("id_usuario"));
				user.setNome(rs.getString("nome_usuario"));
				tweet.setUsuario(user);
			}
			rs.close();
			ps.close();

			return tweet;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar o tweet", e);
		}
	}
	
	public List<Tweet> pesquisar(TweetSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			List<Tweet> tweets = new ArrayList<>();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT t.id, t.conteudo, t.data_postagem, t.id_usuario, u.nome as nome_usuario FROM tweet t ");
			sql.append("JOIN usuario u on t.id_usuario = u.id ");
			
			this.criarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			
			this.adicionarParametros(ps, seletor);

			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Tweet tweet = new Tweet();
				tweet = new Tweet();
				tweet.setId(rs.getInt("id"));
				tweet.setConteudo(rs.getString("conteudo"));

				Calendar data = new GregorianCalendar();
				data.setTime(rs.getTimestamp("data_postagem"));
				tweet.setData(data);

				Usuario user = new Usuario();
				user.setId(rs.getInt("id_usuario"));
				user.setNome(rs.getString("nome_usuario"));
				tweet.setUsuario(user);

				tweets.add(tweet);
			}
			rs.close();
			ps.close();

			return tweets;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao pesquisar os tweets", e);
		}
	}
	
	public Long contar(TweetSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Long id = 0L;

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(t.id) as total FROM tweet t ");
			sql.append("JOIN usuario u on t.id_usuario = u.id ");
			
			this.criarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			
			this.adicionarParametros(ps, seletor);

			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				id = rs.getLong("total");
			}
			rs.close();
			ps.close();

			return  id;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao contar os tweets", e);
		}
	}

	public List<Tweet> listarTodos() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		return this.pesquisar( new TweetSeletor() );
	}

	private void criarFiltros(StringBuilder sql, TweetSeletor seletor) {

		if (seletor.possuiFiltro()) {
			sql.append(" WHERE ");
			boolean filtros = false;
			if (seletor.getId() != null) {
				sql.append("t.id = ? ");
				filtros = true;

			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
				if (filtros) {
					sql.append("AND ");
				} else {
					filtros = true;
				}
				sql.append("t.conteudo like ? ");
			}
			if (seletor.getData() != null) {
				if (filtros) {
					sql.append("AND ");
				} else {
					filtros = true;
				}
				sql.append("t.data_publicada = ? ");
			}
			if (seletor.getIdUsuario() != null) {
				if (filtros) {
					sql.append("AND ");
				} else {
					filtros = true;
				}
				sql.append("t.id_usuario = ? ");
			}

		}
	}

	private void adicionarParametros(PreparedStatement ps, TweetSeletor seletor) throws SQLException {
		int indice = 1;

		if (seletor.possuiFiltro()) {
			if (seletor.getId() != null) {
				ps.setInt(indice++, seletor.getId());
			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
				ps.setString(indice++, String.format("%%%s%%", seletor.getConteudo()));
			}
			if (seletor.getData() != null) {
				ps.setTimestamp(indice++, new Timestamp(seletor.getData().getTimeInMillis()));
			}
			if (seletor.getIdUsuario() != null) {
				ps.setInt(indice++, seletor.getIdUsuario());
			}
		}
	}

}
