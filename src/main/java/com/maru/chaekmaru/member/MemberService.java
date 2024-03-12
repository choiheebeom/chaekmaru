package com.maru.chaekmaru.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.maru.chaekmaru.config.Config;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MemberService {

	@Autowired
	IMemberDaoForMybatis memberDao;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JavaMailSender javaMailSender;

	public int createAccountConfirm(MemberDto memberDto) {
		log.info("--createAccountConfirm--");

		int isMember = memberDao.isMember(memberDto.getM_id());

		if (isMember <= 0) {
			memberDto.setM_pw(passwordEncoder.encode(memberDto.getM_pw()));

			int result = memberDao.insertMember(memberDto);

			if (result > 0) {
				return Config.CREATE_ACCOUNT_SUCCESS;
			} else {
				return Config.CREATE_ACCOUNT_FAIL;
			}

		} else {
			return Config.ID_ALREADY_EXIST;
		}
	}

	/*
	 * public MemberDto loginConfirm(MemberDto memberDto) {
	 * log.info("--loginConfirm--");
	 * 
	 * MemberDto selectedMemberDtoById =
	 * memberDao.selectMember(memberDto.getM_id());
	 * 
	 * if (selectedMemberDtoById != null &&
	 * passwordEncoder.matches(memberDto.getM_pw(),
	 * selectedMemberDtoById.getM_pw())) {
	 * log.info(selectedMemberDtoById.getM_id());
	 * 
	 * return selectedMemberDtoById; } else { return null; } }
	 */

	public MemberDto modifyConfirm(MemberDto memberDto) {
		log.info("modifyConfirm()");

		// #TODO isMember
		int isMember = memberDao.isMember(memberDto.getM_id());
		
		if (isMember > 0) {
			int result = memberDao.updateMemberForModify(memberDto);

			if (result > 0) {
				return memberDao.selectMember(memberDto.getM_id());
			}
		} else {
            
        }

		return null;
	}

	public int memberDeleteConfirm(String m_id) {
		log.info("memberDeleteConfirm()");

		// #TODO isMember
		
		int result = memberDao.deleteMember(m_id);
		if(result > 0) {
			// #TODO 포인트, 리뷰, 장바구니, 찜목록 삭제
			return Config.MEMBER_DELETE_SUCCESS;
		} else {
			return Config.MEMBER_DELETE_FAIL;
		}
	}

	public String findIdByNameAndEmail(String name, String email) {
		log.info("findIdByNameAndEmail()");
		
		return memberDao.findIdByNameAndEmail(name, email);
	}

	public void sendEmail(String email, String message) {
		log.info("sendEmail()");
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject("the result of your request");
		mailMessage.setText(message);

		javaMailSender.send(mailMessage);
	}

	public MemberDto findMember(String id, String name, String email) {
		log.info("findMember()");
		return (MemberDto) memberDao.selectMemberByFindPw(id, name, email);
	}

	public int pwModifyConfirm(String id, String m_pw) {
		log.info("pwModifyConfirm()");

		int result = memberDao.pwModifyConfirm(id, passwordEncoder.encode(m_pw));

		if (result > 0) {
			return Config.PW_MODIFY_SUCCESS;
		} else {
			return Config.PW_MODIFY_FAIL;
		}
	}

	public int refreshPoint(HttpSession session) {
		MemberDto loginedMemberDto = (MemberDto) session.getAttribute(Config.LOGINED_MEMBER_INFO);
		
		int point = 0;
		
		if (loginedMemberDto != null) {
			point = memberDao.selectNowPoint(loginedMemberDto.getM_id());
			loginedMemberDto.setPoint(point);

			session.setAttribute(Config.LOGINED_MEMBER_INFO, loginedMemberDto);
		}

		return point;
	}

}
