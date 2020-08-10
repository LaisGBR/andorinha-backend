package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.Stateless;

import model.Comentario;
import model.Tweet;
import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;
import model.seletor.ComentarioSeletor;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository {

	public void inserir(Comentario comentario) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			int id = this.recuperarProximoValorDaSequence("seq_comentario");
			comentario.setId(id);

			Calendar calendar = Calendar.getInstance();

			PreparedStatement ps = c.prepareStatement(
					"insert into comentario (id, conteudo, data_postagem, id_usuario, id_tweet) values (?,?,?,?,?)");
			ps.setInt(1, comentario.getId());
			ps.setString(2, comentario.getConteudo());
			ps.setTimestamp(3, new Timestamp(calendar.getTimeInMillis()));
			ps.setInt(4, comentario.getUsuario().getId());
			ps.setInt(5, comentario.getTweet().getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o comentário", e);
		}
	}

	public void atualizar(Comentario comentario) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Calendar hoje = Calendar.getInstance();

			PreparedStatement ps = c
					.prepareStatement("update comentario set conteudo = ?, data_postagem = ? where id = ?");
			ps.setString(1, comentario.getConteudo());
			ps.setTimestamp(2, new Timestamp(hoje.getTimeInMillis()));
			ps.setInt(3, comentario.getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o comentário", e);
		}
	}

	public void remover(int id) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {

			PreparedStatement ps = c.prepareStatement("delete from comentario where id = ?");
			ps.setInt(1, id);
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o comentário", e);
		}

	}

	public Comentario consultar(int id) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Comentario comentario = null;

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT c.id, c.conteudo, c.data_postagem, c.id_usuario, u.nome as nome_usuario, ");
			sql.append("c.id_tweet, t.conteudo as conteudo_tweet, t.id_usuario as id_usuario_tweet,  ");
			sql.append("(SELECT u.nome as nome_usuario_tweet FROM tweet t ");
			sql.append("JOIN usuario u on t.id_usuario = u.id ");
			sql.append("WHERE t.id = c.id_tweet ) ");
			sql.append("FROM comentario c ");
			sql.append("JOIN usuario u on c.id_usuario = u.id ");
			sql.append("JOIN tweet t on c.id_tweet = t.id ");
			sql.append("WHERE c.id = ? ");

			PreparedStatement ps = c.prepareStatement(sql.toString());
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				comentario = new Comentario();
				comentario.setId(rs.getInt("id"));
				comentario.setConteudo(rs.getString("conteudo"));

				Calendar data = new GregorianCalendar();
				data.setTime(rs.getTimestamp("data_postagem"));
				comentario.setData(data);

				Usuario user = new Usuario();
				user.setId(rs.getInt("id_usuario"));
				user.setNome(rs.getString("nome_usuario"));
				comentario.setUsuario(user);

				Tweet tweet = new Tweet();
				tweet.setId(rs.getInt("id_tweet"));
				tweet.setConteudo(rs.getString("conteudo_tweet"));
				
				Calendar dataTweet = new GregorianCalendar();
				dataTweet.setTime( rs.getTimestamp("data_postagem") );
				tweet.setData(dataTweet);
				
				Usuario userTweet = new Usuario();
				userTweet.setId(rs.getInt("id_usuario_tweet"));
				userTweet.setNome(rs.getString("nome_usuario_tweet"));
				
				tweet.setUsuario(userTweet);
				comentario.setTweet(tweet);
			}
			rs.close();
			ps.close();

			return comentario;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar o comentário", e);
		}
	}
	
	public List<Comentario> pesquisar(ComentarioSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			List<Comentario> comentarios = new ArrayList<>();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT c.id, c.conteudo, c.data_postagem, c.id_usuario, u.nome as nome_usuario, ");
			sql.append("c.id_tweet, t.conteudo as conteudo_tweet, t.id_usuario as id_usuario_tweet,  ");
			sql.append("(SELECT u.nome as nome_usuario_tweet FROM tweet t ");
			sql.append("JOIN usuario u on t.id_usuario = u.id ");
			sql.append("WHERE t.id = c.id_tweet ) ");
			sql.append("FROM comentario c ");
			sql.append("JOIN usuario u on c.id_usuario = u.id ");
			sql.append("JOIN tweet t on c.id_tweet = t.id ");
			
			this.criarFiltros(sql, seletor);

			PreparedStatement ps = c.prepareStatement(sql.toString());
			
			this.adicionarParametros(ps, seletor);

			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Comentario comentario = new Comentario();
				comentario.setId(rs.getInt("id"));
				comentario.setConteudo(rs.getString("conteudo"));

				Calendar data = new GregorianCalendar();
				data.setTime(rs.getTimestamp("data_postagem"));
				comentario.setData(data);

				Usuario user = new Usuario();
				user.setId(rs.getInt("id_usuario"));
				user.setNome(rs.getString("nome_usuario"));
				comentario.setUsuario(user);
				
				Tweet tweet = new Tweet();
				tweet.setId(rs.getInt("id_tweet"));
				tweet.setConteudo(rs.getString("conteudo_tweet"));
				
				Calendar dataTweet = new GregorianCalendar();
				dataTweet.setTime( rs.getTimestamp("data_postagem") );
				tweet.setData(dataTweet);
				
				Usuario userTweet = new Usuario();
				userTweet.setId(rs.getInt("id_usuario_tweet"));
				userTweet.setNome(rs.getString("nome_usuario_tweet"));
				
				tweet.setUsuario(userTweet);
				comentario.setTweet(tweet);

				comentarios.add(comentario);
			}
			rs.close();
			ps.close();

			return comentarios;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao pesquisar os comentários", e);
		}
	}
	
	public Long contar(ComentarioSeletor seletor) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Long id = 0L;

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(c.id) as total ");
			sql.append("FROM comentario c ");
			sql.append("JOIN usuario u on c.id_usuario = u.id ");
			sql.append("JOIN tweet t on c.id_tweet = t.id ");
			
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
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao contar os comentários", e);
		}
	}

	public List<Comentario> listarTodos() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		return this.pesquisar( new ComentarioSeletor() );
	}
	
	private void criarFiltros(StringBuilder sql,ComentarioSeletor seletor ){
		
		if(seletor.possuiFiltro()) {
			sql.append(" WHERE ");
			boolean filtros = false;
			if(seletor.getId()  != null) {
				sql.append("c.id = ? ");
			   filtros = true;
			    
			}
			if(seletor.getConteudo()  != null && !seletor.getConteudo().trim().isEmpty() ) {
				if (filtros) {
					sql.append("AND ");
				}else {
					filtros = true;
				}
				sql.append("c.conteudo like ? ");
			}
			if(seletor.getData()  != null) {
				if (filtros) {
					sql.append("AND ");
				}else {
					filtros = true;
				}
				sql.append("c.data_publicada = ? ");
			}
			if(seletor.getIdUsuario() != null) {
				if (filtros) {
					sql.append("AND ");
				}else {
					filtros = true;
				}
				sql.append("c.id_usuario = ? ");
			}
			if(seletor.getIdTweet() != null) {
				if (filtros) {
					sql.append("AND ");
				}
				sql.append("c.id_tweet = ? ");
			}	
		
		}
	}
	
	private void adicionarParametros(PreparedStatement ps, ComentarioSeletor seletor) throws SQLException {
		int indice = 1;

		if (seletor.possuiFiltro()) {
			if ( seletor.getId() != null ) {
				ps.setInt(indice++, seletor.getId());
			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty() ) {
				ps.setString(indice++, String.format("%%%s%%", seletor.getConteudo()) );
			}
			if(seletor.getData()  != null) {
				ps.setTimestamp(3, new Timestamp(seletor.getData().getTimeInMillis()));
			}
			if ( seletor.getIdUsuario() != null ) {
				ps.setInt(indice++, seletor.getIdUsuario());
			}
			if ( seletor.getIdTweet() != null ) {
				ps.setInt(indice++, seletor.getIdTweet());
			}
		}
	}

}
