package com.codegym.controller;

import com.codegym.exception.NotFoundException;
import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping ("/products")
public class ProductController {
	@Autowired
	private IProductService productService;
	@Autowired
	private ICategoryService categoryService;

	@Value("${file-upload}")
	private String uploadPath;

	@ModelAttribute ("categories")
	public Iterable<Category> categories(){
		return categoryService.findAll();
	}

	@ExceptionHandler(NotFoundException.class)
	public ModelAndView notFoundPage (){
		return new ModelAndView("/error-404");
	}

	@GetMapping ("")
	public ModelAndView showAllProduct (@RequestParam (name = "q")Optional<String> q, @PageableDefault(value = 3) Pageable pageable){
		Page<Product> products;
		ModelAndView modelAndView = new ModelAndView("/product/list");
		if (q.isPresent()){
			products = productService.findAllByNameContaining(q.get(), pageable);
			modelAndView.addObject("q", q.get());
		}
		else {
			products = productService.findAll(pageable);
		}

		modelAndView.addObject("products", products);
		return modelAndView;
	}
	@GetMapping ("/create")
	public ModelAndView showCreateForm (){
		ModelAndView modelAndView = new ModelAndView("/product/create");
		modelAndView.addObject("product", new ProductForm());
		return modelAndView;
	}
	@PostMapping("/create")
	public ModelAndView createProduct (@Validated @ModelAttribute (name = "product") ProductForm productForm, BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return new ModelAndView("/product/create");
		}
		String fileName = productForm.getImage().getOriginalFilename();
		long currentTime = System.currentTimeMillis();
		fileName = currentTime + fileName;
		try {
			FileCopyUtils.copy(productForm.getImage().getBytes(),new File( uploadPath + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Product product = new Product(productForm.getId(), productForm.getName(),productForm.getPrice(), productForm.getDescription(), fileName, productForm.getCategory());
		productService.save(product);
		return new ModelAndView("redirect:/products");
	}
	@GetMapping ("/edit/{id}")
	public ModelAndView showEditForm (@PathVariable Long id) throws NotFoundException {
		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()){
			throw new NotFoundException();
		}
		ModelAndView modelAndView = new ModelAndView("/product/edit");
		modelAndView.addObject("product", product.get());
		return modelAndView;
	}
	@PostMapping ("/edit/{id}")
	public ModelAndView deleteProduct(@PathVariable Long id,@Validated  @ModelAttribute(name = "product") ProductForm productForm, BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()){
			return new ModelAndView("/product/edit");
		}
		Optional<Product>product = productService.findById(id);
		MultipartFile img = productForm.getImage();
		if (product.isPresent()){
			Product oldProduct = product.get();
			if (img.getSize()!=0){
				String fileName = img.getOriginalFilename();
				long currentTime = System.currentTimeMillis();
				fileName = currentTime + fileName;
				oldProduct.setImage(fileName);
				try {
					FileCopyUtils.copy(img.getBytes(), new File(uploadPath+fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oldProduct.setPrice(productForm.getPrice());
			oldProduct.setDescription(productForm.getDescription());
			oldProduct.setName(productForm.getName());
			productService.save(oldProduct);
			return new ModelAndView("redirect:/products");
		}
		throw new NotFoundException();
	}
	@GetMapping ("/delete/{id}")
	public ModelAndView showDeleteForm (@PathVariable Long id) throws NotFoundException {
		ModelAndView modelAndView = new ModelAndView("/product/delete");
		Optional<Product>product = productService.findById(id);
		if (!product.isPresent()){
			throw new NotFoundException();
		}
		else {
			modelAndView.addObject("product",product.get());
			return modelAndView;
		}
	}
	@PostMapping ("/delete/{id}")
	public ModelAndView deleteProduct (@PathVariable Long id) throws NotFoundException {
		Optional<Product>product = productService.findById(id);
		if (product.isPresent()){
			File file = new File(product.get().getImage());
			if (file.exists()){
				file.delete();
			}
			productService.deleteById(id);
			return new ModelAndView("redirect:/products");
		}
		throw new NotFoundException();
	}
	@GetMapping("/{id}")
	public ModelAndView viewProduct (@PathVariable Long id) throws NotFoundException {
		Optional<Product> product = productService.findById(id);
		if (!product.isPresent()){
			throw new NotFoundException();
		}
		ModelAndView modelAndView = new ModelAndView("product/view");
		modelAndView.addObject("product", product.get());
		return modelAndView;
	}
	@GetMapping ("/search")
	public ModelAndView showProductSearch (@RequestParam("min") Double min, @RequestParam ("max") Double max){
		Iterable<Product> products = productService.findAllByPriceBetween(min, max);
		ModelAndView modelAndView = new ModelAndView("/product/list");
		modelAndView.addObject("products", products);
		return modelAndView;
	}
}
