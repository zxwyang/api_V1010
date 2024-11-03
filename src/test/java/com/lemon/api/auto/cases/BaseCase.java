package com.lemon.api.auto.cases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.apache.log4j.jmx.LoggerDynamicMBean;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.lemon.api.auto.pojo.Api;
import com.lemon.api.auto.pojo.Case;
import com.lemon.api.auto.util.AuthorizationUtil;
import com.lemon.api.auto.util.DBUtil;
import com.lemon.api.auto.util.ExcelUtil;
import com.lemon.api.auto.util.HttpUtil;
import com.lemon.api.auto.util.ResponseValidateUtil;



/**���ࣨ���ࣩ
 * ��Register��LoginCase��iniData������test1��������ͬ��
 * �������ｫ������������װΪ����
 * ���ٴ���ĸ�����
 * @author Administrator
 */
public class BaseCase {
	//����һ��˽�ж���logger��������־�༭
	private Logger logger = Logger.getLogger(BaseCase.class);

	// ���׼�ִ��֮ǰִ�У�@BeforeSuite�ǻ��ڲ����׼��ģ�����ֻ��ִ��һ��
	@BeforeSuite
	// ITestContext�����������Ķ��󣻴�����������Ե����ڣ�������Ϊһ���������
	// �൱�ڽ��������ϴ������������
	// ֻ����testngע��ķ�������ֱ������������ITestContext
	public void iniData(ITestContext context) {
		logger.info("*************��ʼִ�в����׼�*************");
		// ������һ��������
		List<Api> apis = ExcelUtil.readPojos(Api.class, 0);
		logger.info("��ȡ���нӿ���Ϣ�����浽������");
		// �����ڶ���������
		List<Case> cases = ExcelUtil.readPojos(Case.class, 1);
		logger.info("��ȡ����������Ϣ�����浽������");
		// ��ȡ�����Ķ���
		context.setAttribute("apis", apis);
		context.setAttribute("cases", cases);
		// �������ݿ�����
		DBUtil.loadDBconfig();
		logger.info("�������ݿ�����");
	}

	/**
	 * call�������ڽӿڲ����ĵ��� ����call��������ȡ����
	 * 
	 * @Test�ǻ��ڲ�����ģ����Ի��������ִ�ж�ִ��
	 */
	@Test(dataProvider = "datas")
	public void test1(Case cs, Api api) {
		logger.info("===========��ʼִ�в��Է���===========");
		logger.info("��ʼ��ѯ����֤�ֶε�����");
		// ��ѯ����֤�ֶε����ݣ��ӿڵ���ǰ�ı�������֤��
		String dbQueryResult = DBUtil.queryValidateFields(cs);

		// �жϱ���֤�����Ƿ�Ϊ�գ���Ϊ�յĻ����ٽ��õ��Ĳ�ѯ����֤�ֶλ�д��Excel����������
		if (dbQueryResult != null) {
			ExcelUtil.saveWriteBackData(cs.getCaseId(), "QueryResultBefore", dbQueryResult);
		}

		// ��ȡÿ���������ص�״̬�롢��Ӧͷ����Ӧ����
		Map<String, Object> resultMap = HttpUtil.call(cs, api);

		// �жϵ�ǰ����ִ�е������Ƿ�Ϊ��¼�ӿڵ���������
		if ("2".equals(api.getApiId()) && "1".equals(cs.getIsPositive())) {
			// ��������������ͻ�ȡ��½��֤�ļ�Ȩ����
			AuthorizationUtil.storeAuthorization(resultMap);
		}

		// �ӿ���Ӧ������֤���жϸýӿ��Ƿ�ͨ��
		String validateResult = ResponseValidateUtil.validateResponseData(cs, resultMap);
		logger.info("�ӿ���Ӧ���ݵ���֤���Ϊ����"+validateResult+"��");

		// ���ñ����д���ݵķ���saveWriteBackData
		// ����ӿ���֤���ԵĻ�д����
		ExcelUtil.saveWriteBackData(cs.getCaseId(), "ResponseValidationResult", validateResult);

		// ��д�ӿ���Ӧ���ĵķ���
		// ��ȡ��Ӧ����
		String result = (String) resultMap.get("result");
		// ����ӿ���Ӧ���ĵĻ�д����
		ExcelUtil.saveWriteBackData(cs.getCaseId(), "ActualResponseData", result);

		// ��ѯ����֤�ֶε����ݣ��ӿڵ��ú�ı�������֤�� ����ǰ��ı���dbQueryResult
		// �൱�� String a = "hello"; a = "word"; a��ֵ��Ϊword
		dbQueryResult = DBUtil.queryValidateFields(cs);
		// �ٽ��õ��Ĳ�ѯ����֤�ֶλ�д��Excel����������
		if (dbQueryResult != null) {
			ExcelUtil.saveWriteBackData(cs.getCaseId(), "QueryResultAfter", dbQueryResult);
		}
		logger.info("===========���Է���ִ�����===========");
	}

	// ׼��һ��finish���������׼�ִ����󣬽������е�����ȫ����д��Excel����
	@AfterSuite
	public void finish() {
		// ������д����
		ExcelUtil.batchWriteBackDatas();
		logger.info("*************�����׼�ִ�����*************");
	}

}
