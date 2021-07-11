package com.alkemy.ong.service.Interface;


import com.alkemy.ong.model.Testimonials;

import java.util.List;

public interface ITestimonials {

    public Testimonials findById(Long id);

    public Testimonials save(Testimonials testimonials);

    /* Para el endpoint /testimonials/{id} */
    public void deleteById(Long id);



}
