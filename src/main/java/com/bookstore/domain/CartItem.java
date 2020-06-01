package com.bookstore.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CartItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int qty;
    private BigDecimal subTotal;

    @OneToOne
    private Book book;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookToCartItem> bookToCartItemList = new ArrayList<>();


    @ManyToOne
    private ShoppingCart shoppingCart;

    @ManyToOne
    private Order order;

    public CartItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<BookToCartItem> getBookToCartItemList() {
        return bookToCartItemList;
    }

    public void setBookToCartItemList(List<BookToCartItem> bookToCartItemList) {
        this.bookToCartItemList = bookToCartItemList;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
