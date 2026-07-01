package com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.specification;

import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.enums.Type;
import com.luanmvcosta0.desafio_tecnico_engeman_be.modules.property.model.PropertyEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification {

    public static Specification<PropertyEntity> filters(Type type, BigDecimal minPrice, BigDecimal maxPrice, Integer rooms, String name) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (rooms != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rooms"), rooms));
            }
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
