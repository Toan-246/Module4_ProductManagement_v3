package com.codegym.model;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductForm {
	private Long id;

	@NotEmpty (message = "Không được để trống")
	@Size (min = 1, max = 10)
	private String name;

	@NotNull (message = "Không được để trống")
	private Double price;

	@NotEmpty (message = "Không được để trống")
	private String description;
	private MultipartFile image;

	Category category;

	public ProductForm() {
	}

	public ProductForm(Long id, String name, Double price, String description, MultipartFile image, Category category) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.image = image;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
