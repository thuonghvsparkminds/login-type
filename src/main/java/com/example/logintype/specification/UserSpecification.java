package com.example.logintype.specification;

import com.example.logintype.entity.User;
import com.example.logintype.entity.User_;
import com.example.logintype.entity.enumrated.StatusEnum;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public Specification<User> getUsersList(){

        Specification<User> condition = Specification.where((root, query, criteriaBuilder) -> {

            var userNonVerify = criteriaBuilder.equal(root.get(User_.STATUS), StatusEnum.UNVERIFIED);
            var userActive = criteriaBuilder.equal(root.get(User_.STATUS), StatusEnum.ACTIVE);
            return criteriaBuilder.and(userNonVerify, userActive);
        });

        return condition;
    }
}
