package com.example.demo.controller;

import com.example.demo.common.Mapper;
import com.example.demo.common.dto.ExceptionDto;
import com.example.demo.common.web.UriBuilder;
import com.example.demo.dto.DepartmentDto;
import com.example.demo.model.Department;
import com.example.demo.service.DepartmentNotFoundException;
import com.example.demo.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

@RestController
@RequestMapping("/departments")
@Api(description = "Departments resource")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final Mapper mapper;

    public DepartmentController(DepartmentService departmentService, Mapper mapper) {
        this.departmentService = departmentService;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("List all departments")
    public List<DepartmentDto> list() {
        return mapper.map(departmentService.getDepartments(), DepartmentDto.class);
    }

    @RequestMapping(method = RequestMethod.GET, path = "starting_with/{prefix}")
    @ApiOperation("List all departments starting with the given prefix")
    public List<DepartmentDto> listStartingWith(@PathVariable("prefix") String prefix) {
        return mapper.map(departmentService.getDepartmentsStartingWith(prefix), DepartmentDto.class);
    }

    @RequestMapping(method = RequestMethod.GET, path = "ending_with/{postfix}")
    @ApiOperation("List all departments ending with the given postfix")
    public List<DepartmentDto> listEndingWith(@PathVariable("postfix") String postfix) {
        return mapper.map(departmentService.getDepartmentsEndingWith(postfix), DepartmentDto.class);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Add new department")
    public ResponseEntity add(@Valid @RequestBody DepartmentDto dto) {
        Department department = departmentService.addDepartment(mapper.map(dto, Department.class));
        return created(UriBuilder.requestUriWithId(department.getId())).build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "{id}")
    @ApiOperation("Get departments")
    public DepartmentDto getDepartment(@PathVariable("id") Long id) {
        return mapper.map(departmentService.getDepartment(id), DepartmentDto.class);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "{id}")
    @ApiOperation("Get departments")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, path = "{id}")
    @ApiOperation("Update department")
    public ResponseEntity updateDepartment(@RequestBody DepartmentDto dto, @PathVariable("id") Long id) {
        Department department = mapper.map(dto, Department.class);
        department.setId(id);
        departmentService.updateDepartment(department);
        return noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, path = "normalized")
    @ApiOperation("Replace name with id")
    public ResponseEntity replaceNameWithId() {
        departmentService.replaceNameWithId();
        return noContent().build();
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity handleException(DepartmentNotFoundException ex, Locale locale) {
        ExceptionDto dto = mapper.map(ex, locale);
        return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
    }


}
