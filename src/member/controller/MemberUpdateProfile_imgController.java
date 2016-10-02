package member.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.oreilly.servlet.MultipartRequest;

import member.db.MemberDAO;
import member.db.MemberDTO;

@Controller
public class MemberUpdateProfile_imgController
{

	@Autowired
	MemberDAO memberDAO;

	@RequestMapping(value = "updateProfile_img.do", method = RequestMethod.POST)
	public ModelAndView memberUpdateProfile_imgPro(HttpServletRequest req, HttpServletResponse resp) throws SQLException
	{
		ModelAndView mav = new ModelAndView();
		// MultipartRequest�� ������ �ް� lastIndexOf�� Ȯ���ڸ� ���Ѵ�.
		// if(jpg, png, bmp) �� �ƴ϶�� return

		// 2. MultipartRequest��ü�� id, ���������� �޾� upPath�� ������ ����Ѵ�.
		// �ش����� ������ filereader�� �о���̰� ������ ��Ƶд�.
		// ������ ��ϵ� ������ �����ϰ�, id.jpg�� ����Ѵ�.
		
		
		
		
		
		
		MultipartRequest mr = null;
		String upPath = req.getServletContext().getRealPath("/profile_img/"); //
		File file = new File(upPath);
		if(file.exists()){}else{
			if(file.mkdirs()){
				System.out.println("������");
			}
		}
		
		String id = "", profile_img = "";
		try
		{
			mr = new MultipartRequest(req, upPath, 10 * 1024 * 1024, "EUC-KR");
			profile_img = mr.getFilesystemName("profile_img");
			id = mr.getParameter("id");

			System.out.println("MemberUpdateProfile_imgController mr.request id : " + id);
			System.out.println("MemberUpdateProfile_imgController mr.request filename : " + profile_img);
			System.out.println("upPath : " + upPath);

			if (checkFileExtension(profile_img))
			{
				int res = -1;
				File existFile = new File(upPath + id + ".jpg");
				if (existFile.exists())
				{// ������ ��ϵ� ������ �����ϴٸ�
					if (existFile.delete())
					{
						System.out.println("������");
						res = memberDAO.updateProfile_img(id, id + ".jpg");
					} else
					{
						System.out.println("�����ȵ�");
					}
				} else
				{// ������ ��ϵ� ������ ���ٸ�
					res = memberDAO.updateProfile_img(id, id + ".jpg");
				}

				if (res > 0)
				{// ����
					mav.addObject("profile_img", true);

					convertFile(mr, upPath, id); // ���Ϻ�ȯ
					
					HttpSession session = req.getSession();
					
					MemberDTO dto = (MemberDTO)session.getAttribute("loginId");
					
					dto = memberDAO.getMemberAdmin((int) dto.getNum());
					
					session.setAttribute("loginId", dto);
					
				} else
				{// ����
					mav.addObject("profile_img", false);
				}
			} else if (!checkFileExtension(profile_img))
			{
				mav.addObject("fileCheckResult", false);
				File deleteFile = new File(upPath + mr.getFilesystemName("profile_img"));

				if (deleteFile.delete())
				{
					System.out.println("Ȯ���ڿ���! ���ϻ����մϴ�.");
				} else
				{
					System.out.println("Ȯ���ڿ���! ���ϻ��� ����!");
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		mav.setViewName("member_MyPage.do?mode=myPageMain");
		return mav;
	}

	private boolean checkFileExtension(String fileName)
	{
		System.out.println("checkFileExtension in fileName : " + fileName);
		String ext = "", newfname, pureFileName = "";
		int index = fileName.lastIndexOf(".");
		System.out.println("checkFileExtension in index :" + index);

		if (index != -1)
		{
			pureFileName = fileName.substring(0, index);
			ext = fileName.substring(index + 1);
			System.out.println("���ϸ�: " + fileName + ", Ȯ����: " + ext);
			newfname = fileName + "_s." + ext;
			System.out.println(newfname);
		}
		System.out.println("���ϸ�: " + fileName + ", Ȯ����: " + ext);

		if (ext.equals("jpg") || ext.equals("bmp") || ext.equals("png"))
		{
			return true;
		} else
		{
			return false;
		}
	}

	private void convertFile(MultipartRequest mr, String upPath, String id)
	{
		File file = mr.getFile("profile_img");

		System.out.println("convertFile in file :" + file);
		System.out.println("convertFile in file.getName() :" + file.getName());
		System.out.println("convertFile in file.getPath() :" + file.getPath());

		// FileWriter fw = new FileWriter(file);
		// fw.
		//
		// File newFile = new File(upPath+id+".jpg");
		// newFile.

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try
		{
			fis = new FileInputStream(file.getPath());
			fos = new FileOutputStream(upPath + id+".jpg");
			byte[] buffer = new byte[512];
			int readcount = 0;
			while ((readcount = fis.read(buffer)) != -1)
			{
				fos.write(buffer, 0, readcount);
			}
			System.out.println("���簡 �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e)
		{
			System.out.println(e);
		} finally
		{
			try
			{
				fis.close();
			} catch (IOException e){}
			try
			{
				fos.close();
			} catch (IOException e){}
			if(file.delete()){
				System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�.");
			}else{
				System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�. [ ���� ] ");
			}
		}
	}

}
