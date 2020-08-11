package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public class AbstractCrudRepository {
	
	@Resource(name = "andorinhaDS")
	protected DataSource ds;
	
	@PersistenceContext
	protected EntityManager em;

	protected Connection abrirConexao() throws ErroAoConectarNaBaseException {
		try {
			return ds.getConnection();	
		} catch (SQLException e) {
			throw new ErroAoConectarNaBaseException("Ocorreu um erro ao acessar a base de dados", e);
		}
	}
	
	protected int recuperarProximoValorDaSequence(String nomeSequence) throws ErroAoConsultarBaseException, ErroAoConectarNaBaseException {
		try(Connection c = this.abrirConexao()){
			
			PreparedStatement ps = c.prepareStatement("select nextval(?)");
			ps.setString(1, nomeSequence);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			throw new ErroAoConsultarBaseException("Erro ao recuperar próximo valor da sequence" + nomeSequence, null);
			
		} catch(SQLException e) {
			throw new ErroAoConectarNaBaseException("Ocorreu um erro ao acessar a base de dados", e);
		}
	}

}
