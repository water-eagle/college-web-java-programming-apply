package com.springmvc.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
					SELECT b_bookId, b_name, b_unitPrice, b_author, b_description, b_publisher,
					       b_category, b_unitsInStock, b_releaseDate, b_condition
					FROM book
					WHERE b_category LIKE CONCAT('%', ?, '%')
				""";

		List<Book> listOfBooks = this.template.query(sql, new BookRowMapper(), category);

		return listOfBooks;
	}

	@Override
	public Set<Book> getBookListByFilter(Map<String, List<String>> filter) {
		Set<Book> booksByPublisher = new HashSet<Book>();
		Set<Book> booksByCategory = new HashSet<Book>();

		Set<String> booksByFilter = filter.keySet();

		if (booksByFilter.contains("publisher")) {
			for (int j = 0; j < filter.get("publisher").size(); j++) {
				String publisherName = filter.get("publisher").get(j);
				String sql = """
							SELECT b_bookId, b_name, b_unitPrice, b_author, b_description,
							       b_publisher, b_category, b_unitsInStock, b_releaseDate, b_condition
							FROM book
							WHERE b_publisher LIKE '%' || ? || '%'
						""";
				booksByPublisher.addAll(this.template.query(sql, new BookRowMapper(), publisherName));
			}

		}
		if (booksByFilter.contains("category")) {
			for (int i = 0; i < filter.get("category").size(); i++) {
				String category = filter.get("category").get(i);
				String sql = """
							SELECT b_bookId, b_name, b_unitPrice, b_author, b_description,
							       b_publisher, b_category, b_unitsInStock, b_releaseDate, b_condition
							FROM book
							WHERE b_category LIKE '%' || ? || '%'
						""";
				booksByCategory.addAll(this.template.query(sql, new BookRowMapper(), category));
			}
		}

		// booksByPublisher n booksByCategory
		booksByCategory.retainAll(booksByPublisher);

		return booksByCategory;
	}

}
