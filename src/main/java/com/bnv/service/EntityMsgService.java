
package com.bnv.service;

import com.bnv.model.Response;
import oracle.net.aso.s;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.*;

@Service
public class EntityMsgService {
    private static String _header = "pkg_parse_json.parse_header_json";
    private static String _thongtinchung = "pkg_parse_json.parse_ns_thongtinchung_json";
    private static String _tuyendungquatrinhcongtac = "pkg_parse_json.parse_ns_tuyendung_qtct_json";
    private static String _quatrinhcongtacs = "pkg_parse_json.parse_ns_quatrinhcongtac_json";
    private static String _luongphucap = "pkg_parse_json.parse_ns_luongphucap_json";
    private static String _luongs = "pkg_parse_json.parse_ns_luong_json";
    private static String _phucaps = "pkg_parse_json.parse_ns_phucap_json";
    private static String _trinhdodaotaoboiduong = "pkg_parse_json.parse_ns_daotaoboiduong_json";
    private static String _daotaoboiduongs = "pkg_parse_json.parse_daotaoboiduong_json";
    private static String _tinhocs = "pkg_parse_json.parse_tinhoc_json";
    private static String _ngoaingus = "pkg_parse_json.parse_ngoaingu_json";
    private static String _thongtinkhac = "pkg_parse_json.parse_ns_thongtinkhac_json";
    private static String _danhgiaphanloais = "pkg_parse_json.parse_danhgiaphanloai_json";

    private static String _xoahosonhansu = "pkg_parse_json.parse_ns_xoahosonhansu_json";
    private static String _sohieucbccvc_bndp = "PKG_SELECT_JSON.SELECT_NS_HOSONHANSU_JSON";

    @PersistenceContext
    private EntityManager entityManager;


//    @Autowired
//    private EntityManagerFactory entityManagerFactory;

    // gọi thủ tục trong sql
    public String callStoreProcedure(String storeProcedureName, String i_madonvi, String i_json) {
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
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    public StoredProcedureQuery storeProcedures(EntityManager em, String storeProcedureName, String i_madonvi, String i_json) {
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery(storeProcedureName);
        storedProcedure.registerStoredProcedureParameter("i_madonvi", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("i_json", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("u_ret", String.class, ParameterMode.OUT);
        storedProcedure.setParameter("i_madonvi", i_madonvi);
        storedProcedure.setParameter("i_json", i_json);
        storedProcedure.execute();
        String outMessage = (String) storedProcedure.getOutputParameterValue("u_ret");

        System.out.println("---------------------input \n" + i_json.toString());
        System.out.println("---------------------output \n" + outMessage.toString());

        return storedProcedure;
    }


}

