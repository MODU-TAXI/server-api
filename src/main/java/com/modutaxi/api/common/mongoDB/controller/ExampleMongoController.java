package com.modutaxi.api.common.mongoDB.controller;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.GlobalErrorCode;
import com.modutaxi.api.common.mongoDB.dao.ExampleMongoDao;
import com.modutaxi.api.common.mongoDB.repository.ExampleMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@RestController
@RequiredArgsConstructor
//@RequestMapping("/mongo")
public class ExampleMongoController {
    private final ExampleMongoRepository exampleMongoRepository;

//    @GetMapping("/save/{firstName}/{lastName}")
    public ExampleMongoDao save(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
        ExampleMongoDao exampleMongoDao = ExampleMongoDao.builder().firstName(firstName).lastname(lastName).build();
        ExampleMongoDao res = exampleMongoRepository.save(exampleMongoDao);
        return res;
    }

//    @GetMapping("/get/{key}")
    public ExampleMongoDao get(@PathVariable("key") String key) {
        ExampleMongoDao res = exampleMongoRepository.findById(key).orElseThrow(
            () -> new BaseException(GlobalErrorCode.NOT_VALID_ARGUMENT_ERROR)
        );
        return res;
    }

//    @DeleteMapping("/delete/{key}")
    public void delete(@PathVariable("key") String key) {
        try {
            exampleMongoRepository.deleteById(key);
        } catch (IllegalArgumentException e) {
            throw new BaseException(GlobalErrorCode.NOT_VALID_ARGUMENT_ERROR);
        }
    }
}
