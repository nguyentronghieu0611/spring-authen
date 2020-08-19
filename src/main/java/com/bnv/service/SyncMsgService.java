
package com.bnv.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

@Service
public class SyncMsgService {
    @PersistenceContext
    public static EntityManager entityManager = null;

    // gọi thủ tục trong sql
    public static String callStoreProcedure(String storeProcedureName, String i_madonvi, String i_json) {
        try {
            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery(storeProcedureName);
            storedProcedure.registerStoredProcedureParameter("i_madonvi", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("i_json", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("u_ret", String.class, ParameterMode.OUT);
            storedProcedure.setParameter("i_madonvi", i_madonvi);
            storedProcedure.setParameter("i_json", i_json);
            storedProcedure.execute();
            String outMessage = (String) storedProcedure.getOutputParameterValue("u_ret");

            System.out.println("---------------------input \n" + i_json.toString());
            System.out.println("---------------------output \n" + outMessage.toString());

            return outMessage.toString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}

