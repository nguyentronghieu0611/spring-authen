/**
 * 
 */
/**
 * @author Dell
 *
 */
package com.bnv.repository;

import com.bnv.model.DAOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

@Repository
//@ComponentScan("com.vnpt.auth.entity")
public interface AdmUserRepository extends JpaRepository<DAOUser, Long> {

	@Transactional
	@Procedure(name="PKG_PARSE_JSON.GET_TOTAL_CARS_BY_MODEL")
	int createUser(@Param("i_json")String i_jsons);

	@Transactional
	@Procedure(procedureName = "PKG_PARSE_JSON.GET_TOTAL_CARS_BY_MODEL", outputParameterName = "u_ret")
	int findUserFullNameIn_OutUsingName(@Param("i_json") String in);

    @Query(value = "SELECT pkg_parse_json.parse_header_json(:i_madonvi, :i_json) FROM dual", nativeQuery = true)
    String FunctionServiceHeader(@Param("i_madonvi" ) String i_madonvi, @Param("i_json" ) String i_json);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0001(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0001(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0002(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0002(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0003(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0003(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0004(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0004(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0005(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0005(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0006(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0006(String i_madonvi, String i_xml);

	@Query(value = "SELECT pkg_parse_xml.PARSE_M0007(:i_madonvi, :i_xml) FROM DUAL", nativeQuery = true)
	public String FunctionServiceM0007(String i_madonvi, String i_xml);

//	public EntityManager entityManager = null;
//	// gọi thủ tục trong sql
//	public static String callStoreProcedure(String storeProcedureName, String i_madonvi, String i_json) {
//		try {
//			StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery(storeProcedureName);
//			storedProcedure.registerStoredProcedureParameter("i_madonvi", String.class, ParameterMode.IN);
//			storedProcedure.registerStoredProcedureParameter("i_json", String.class, ParameterMode.IN);
//			storedProcedure.registerStoredProcedureParameter("u_ret", String.class, ParameterMode.OUT);
//			storedProcedure.setParameter("i_madonvi", i_madonvi);
//			storedProcedure.setParameter("i_json", i_json);
//			storedProcedure.execute();
//			String outMessage = (String) storedProcedure.getOutputParameterValue("u_ret");
//
//			System.out.println("---------------------input \n" + i_json.toString());
//			System.out.println("---------------------output \n" + outMessage.toString());
//
//			return outMessage.toString();
//		} catch (Exception ex) {
//			return ex.getMessage();
//		}
//	}
}