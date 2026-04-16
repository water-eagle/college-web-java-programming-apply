package com.springmvc.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.springmvc.domain.Book;

@Repository
public class BookRepositoryImpl implements BookRepository {
	private JdbcTemplate template;

	public BookRepositoryImpl(JdbcTemplate template) {
		this.template = template;

	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate template) {
		this.template = template;
	}

	@Override
	public List<Book> getAllBookList() {
		String sql = "SELECT b_bookId, b_name, b_unitPrice, b_author, b_description, b_publisher"
				+ ", b_category, b_unitsInStock, b_releaseDate, b_condition FROM book";

		List<Book> listOfBooks = this.template.query(sql, new BookRowMapper());

		return listOfBooks;
	}

	@Override
	public List<Book> getBookListByCategory(String category) {
		String sql = """
					SELECT b_bookId, b_name, b_unitPrice, b_author, b_description, b_publisher, b_category, b_unitsInStock
					b_releaseDate, b_condition
				FROM book
				WHERE b_category like concat('%', ?, '%')
				""";

		List<Book> listOfBooks = this.template.query(sql, new BookRowMapper(), category);

		return listOfBooks;
	}

}
