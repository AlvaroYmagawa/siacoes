package br.edu.utfpr.dv.siacoes.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    @Test
    void getName() {
        Department department = new Department();

        department.setName("Departamento Cornélio Procópio");
        String name = department.getName();

        assertEquals("Departamento Cornélio Procópio", name);
    }
}