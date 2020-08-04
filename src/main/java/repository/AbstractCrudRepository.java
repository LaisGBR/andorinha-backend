package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.exceptions.ErroAoConectarNaBaseException;
import model.exceptions.ErroAoConsultarBaseException;

public class AbstractCrudRepository {

	protected Connection abrirConexao() throws ErroAoConectarNaBaseException {
		try {
			return DriverManager.getConnection("jdbc:postgresql://localhost/andorinha_test", "postgres", "Postgres");		
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
