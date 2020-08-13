package repository;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import model.Tweet;
import model.dto.TweetDTO;
import model.seletor.TweetSeletor;

@Stateless
public class TweetRepository extends AbstractCrudRepository {

	public void inserir(Tweet tweet) {
		tweet.setData(Calendar.getInstance());
		super.em.persist(tweet);
	}

	public void atualizar(Tweet tweet) {
		tweet.setData(Calendar.getInstance());
		super.em.merge(tweet);
	}

	public void remover(int id) {
		Tweet t = this.consultar(id);
		super.em.remove(t);
	}

	public Tweet consultar(int id) {
		return super.em.find(Tweet.class, id);
	}

	public List<Tweet> pesquisar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT t FROM Tweet t  ");
		jpql.append("INNER JOIN FETCH t.usuario u ");

		this.criarFiltros(jpql, seletor);

		Query query = super.em.createQuery(jpql.toString());

		this.adicionarParametros(query, seletor);

		return query.getResultList();
	}
	
	public List<TweetDTO> pesquisarDTO(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT new model.dto.TweetDTO(t.id, t.conteudo, t.data, u.id) FROM Tweet t  ");
		jpql.append("INNER JOIN t.usuario u ");

		this.criarFiltros(jpql, seletor);

		Query query = super.em.createQuery(jpql.toString(), TweetDTO.class);

		this.adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(TweetSeletor seletor) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT count(t) FROM Tweet t ");

		this.criarFiltros(jpql, seletor);

		Query query = super.em.createQuery(jpql.toString());

		this.adicionarParametros(query, seletor);
		
		return (Long)query.getSingleResult();

	}

	public List<Tweet> listarTodos()  {
		return this.pesquisar(new TweetSeletor());
	}

	private void criarFiltros(StringBuilder jpql, TweetSeletor seletor) {

		if (seletor.possuiFiltro()) {
			jpql.append(" WHERE ");
			boolean filtros = false;
			if (seletor.getId() != null) {
				jpql.append("t.id = :id ");
				filtros = true;

			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append("t.conteudo LIKE :conteudo ");
			}
			if (seletor.getData() != null) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append("date(t.data) = :data_postagem ");
			}
			if (seletor.getIdUsuario() != null) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append("t.usuario.id  = :id_usuario ");
			}

		}
	}

	private void adicionarParametros(Query query, TweetSeletor seletor) {
		if (seletor.possuiFiltro()) {
			if (seletor.getId() != null) {
				query.setParameter("id", seletor.getId());				
			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
				query.setParameter("conteudo", String.format("%%%s%%", seletor.getConteudo()) );
			}
			if (seletor.getData() != null) {
				query.setParameter("data_postagem", seletor.getData());
			}
			if (seletor.getIdUsuario() != null) {
				query.setParameter("id_usuario", seletor.getIdUsuario());		
			}
		}
	}

}
