package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Usuario;
import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public class UsuarioRepository extends AbstractCrudRepository {

	public void inserir(Usuario usuario) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			int id = this.recuperarProximoValorDaSequence("seq_usuario");
			usuario.setId(id);

			PreparedStatement ps = c.prepareStatement("insert into usuario (id, nome) values (?,?)");
			ps.setInt(1, usuario.getId());
			ps.setString(2, usuario.getNome());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao inserir o usuário", e);
		}
	}

	public void atualizar(Usuario usuario) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			PreparedStatement ps = c.prepareStatement("update usuario set nome = ? where id = ?");
			ps.setString(1, usuario.getNome());
			ps.setInt(2, usuario.getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao atualizar o usuário", e);
		}
	}

	public void remover(Usuario usuario) throws ErroAoConectarNaBaseException, ErroAoConsultarBaseException {
		try (Connection c = this.abrirConexao()) {

			PreparedStatement ps = c.prepareStatement("delete from usuario where id = ?");
			ps.setInt(1, usuario.getId());
			ps.execute();
			ps.close();

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao remover o usuário", e);
		}

	}

	public Usuario consultar(int id) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			Usuario user = null;

			PreparedStatement ps = c.prepareStatement("select id, nome from usuario where id = ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = new Usuario();
				user.setId(rs.getInt("id"));
				user.setNome(rs.getString("nome"));
			}
			rs.close();
			ps.close();

			return user;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar o usuário", e);
		}
	}

	public List<Usuario> listarTodos() throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try (Connection c = this.abrirConexao()) {

			List<Usuario> users = null;

			PreparedStatement ps = c.prepareStatement("select * from usuario");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				users = new ArrayList<Usuario>();
				Usuario user = new Usuario();
				user.setId(rs.getInt("id"));
				user.setNome(rs.getString("nome"));
				users.add(user);
			}
			rs.close();
			ps.close();

			return users;

		} catch (SQLException e) {
			throw new ErroAoConsultarBaseException("Ocorreu um erro ao consultar todos os usuários", e);
		}
	}

}
