package event.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.oreilly.servlet.MultipartRequest;

import event.db.EventDAO;
import event.db.EventDTO;

@Controller
public class EventController
{
	@Autowired
	EventDAO eventDAO;

	@RequestMapping(value = "/eventHome.do", method = RequestMethod.GET)
	public ModelAndView eventHome(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("event.jsp");
		return mav;
	}

	/* Admin */
	@RequestMapping(value = "/insertEvent.do", method = RequestMethod.GET)
	public ModelAndView insertEventForm(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin/event/eventWriteForm.jsp");
		return mav;

	}

	@RequestMapping(value = "/insertEvent.do", method = RequestMethod.POST)
	public ModelAndView insertEventPro(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();

		EventDTO dto = new EventDTO();
		MultipartRequest mr = null;
		String upPath = req.getServletContext().getRealPath("/event/"); //

		File file = new File(upPath);
		if (file.exists())
		{
		} else
		{
			if (file.mkdirs())
			{
				System.out.println("������");
			}
		}
		mr = new MultipartRequest(req, upPath, 10 * 1024 * 1024, "EUC-KR");

		/* dto setting */
		dto.setTitle(mr.getParameter("title"));
		dto.setContent(mr.getParameter("content"));
		dto.setStartDate(mr.getParameter("startDate"));
		dto.setEndDate(mr.getParameter("endDate"));

		/* file setting */
		String fileName = mr.getFilesystemName("event_img");

		final int seqCurrVal = eventDAO.getSeqCurrVal();

		if (checkFileExtension(fileName))// Ȯ���� �˻�
		{// ���� ����
			int res = -1;
			File existFile = new File(upPath + seqCurrVal + ".jpg");
			if (existFile.exists() && existFile.delete())
			{// ������ ��ϵ� ������ �����ϴٸ�
			}

			File oldFile = new File(upPath + fileName);

			if (oldFile.exists())
			{
				convertFile(mr, upPath, seqCurrVal); // ���Ϻ�ȯ
				oldFile.delete(); // �������ϻ���
				dto.setFileName((seqCurrVal + 1) + ".jpg");

				mav.addObject("uploadResult", true); // <c:if
														// tes="${uploadResult
														// eq true}">
				mav.setViewName("redirect:adminEventList.do");
			}
		} else
		{
			mav.addObject("uploadResult", false); // <c:if tes="${uploadResult
													// eq false}">
			mav.setViewName("redirect:admin/event/eventWriteForm.jsp");
		}

		int res = eventDAO.insertEvent(dto);

		return mav;
	}

	@RequestMapping(value = "/deleteEvent.do", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public ModelAndView deleteEvent(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		int res = eventDAO.deleteEvent(Integer.parseInt(req.getParameter("num")));

		if (res > 0)
		{
			mav.addObject("deleteResult", true);
		} else
		{
			System.out.println("event ������ ���� �߻�!, ��ġ: EventController /deleteEvent.do_POST");
		}
		mav.setViewName("redirect:adminEventList.do");
		return mav;
	}

	@RequestMapping(value = "/updateEvent.do", method = RequestMethod.GET)
	public ModelAndView updateEventForm(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin/event/eventUpdateForm.jsp");
		return mav;
	}

	@RequestMapping(value = "/updateEvent.do", method = RequestMethod.POST)
	public ModelAndView updateEventPro(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		String upPath = req.getServletContext().getRealPath("/event/");
		MultipartRequest mr = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		String fileName = mr.getFilesystemName("event_img");
		
		//���� ���ε��
		mr = new MultipartRequest(req, upPath, 10 * 1024 * 1024, "EUC-KR");

		String oldFile = mr.getFilesystemName("event_img");
		
		EventDTO dto = eventDAO.getEvent(Integer.parseInt(mr.getParameter("num")));

		/* update file ���� */
		if (fileName == null)
		{//���Ϻ��� ����
			dto.setTitle(mr.getParameter("title"));
			dto.setContent(mr.getParameter("content"));
			dto.setStartDate(mr.getParameter("startDate"));
			dto.setEndDate(mr.getParameter("endDate"));
		} else if (fileName != null  && checkFileExtension(fileName))
		{// ���� ���� �߻�, Ȯ���� �˻� ����
			File file = new File(upPath + dto.getFileName());
			
			if (file.exists()){//�������� ������.
				file.delete();
			}
			
			file = mr.getFile("event_img");
			
			try
			{
				fis = new FileInputStream(file.getPath());
				fos = new FileOutputStream(upPath + dto.getNum() + ".jpg");
				byte[] buffer = new byte[512];
				int readcount = 0;
				while ((readcount = fis.read(buffer)) != -1)
				{
					fos.write(buffer, 0, readcount);
				}
				System.out.println("���簡 �Ϸ�Ǿ����ϴ�.");
				File oldFiles = new File(upPath+oldFile);
				oldFiles.delete();
			} catch (Exception e)
			{
				System.out.println(e);
			} finally
			{
				try
				{
					fis.close();
				} catch (IOException e)
				{
				}
				try
				{
					fos.close();
				} catch (IOException e)
				{
				}
				if (file.delete())
				{
					System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�.");
				} else
				{
					System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�. [ ���� ] ");
				}
			}

		} else if (fileName != null  && !checkFileExtension(fileName))
		{// ���� ���� �߻�, Ȯ���� �˻� ����
			mav.addObject("uploadResult", false); // <c:if tes="${uploadResult
			// eq false}">
			mav.setViewName("redirect:adminEventList.do");
		}

		

		int res = eventDAO.updateEvent(dto);

		return mav;
	}

	@RequestMapping(value = "/getEvent.do", method = RequestMethod.GET)
	public ModelAndView getEvent(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		EventDTO dto = eventDAO.getEvent(Integer.parseInt(req.getParameter("num")));
		
		mav.addObject("dto", dto);
		mav.setViewName("redirect:admin/event/eventView.jsp");
		return mav;
	}
	
	
	@RequestMapping(value = "/EventList.do", method = RequestMethod.GET)
	public ModelAndView getEvents(HttpServletRequest req, HttpServletResponse arg1) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		List<EventDTO> list = (List)eventDAO.getEvents();
		System.out.println("EventList.do in list : "+list);
		mav.addObject("list", list);
		mav.setViewName("redirect:admin/event/eventList.jsp");
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

	private void convertFile(MultipartRequest mr, String upPath, int seqCurrVal)
	{
		File file = mr.getFile("event_img");

		System.out.println("convertFile in file :" + file);
		System.out.println("convertFile in file.getName() :" + file.getName());
		System.out.println("convertFile in file.getPath() :" + file.getPath());

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try
		{
			fis = new FileInputStream(file.getPath());
			fos = new FileOutputStream(upPath + (seqCurrVal + 1) + ".jpg");
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
			} catch (IOException e)
			{
			}
			try
			{
				fos.close();
			} catch (IOException e)
			{
			}
			if (file.delete())
			{
				System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�.");
			} else
			{
				System.out.println("��� ���� ���, ��ȯ �۾��� ������ ���� ������ �����մϴ�. [ ���� ] ");
			}
		}
	}

}
