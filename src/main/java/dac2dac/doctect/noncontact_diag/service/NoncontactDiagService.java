package dac2dac.doctect.noncontact_diag.service;

import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayCloseTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.findTodayOpenTime;
import static dac2dac.doctect.common.utils.DiagTimeUtils.isAgencyOpenNow;

import dac2dac.doctect.common.constant.ErrorCode;
import dac2dac.doctect.common.error.exception.NotFoundException;
import dac2dac.doctect.doctor.entity.Doctor;
import dac2dac.doctect.doctor.repository.DepartmentRepository;
import dac2dac.doctect.doctor.repository.DepartmentTagRepository;
import dac2dac.doctect.doctor.repository.DoctorRepository;
import dac2dac.doctect.noncontact_diag.dto.response.DepartmentInfo;
import dac2dac.doctect.noncontact_diag.dto.response.DepartmentListDto;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorDto;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorInfo;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorIntroduction;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorItem;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorListDto;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorReview;
import dac2dac.doctect.noncontact_diag.dto.response.DoctorReviewList;
import dac2dac.doctect.noncontact_diag.dto.response.Top3ReviewTagInfo;
import dac2dac.doctect.review.entity.Review;
import dac2dac.doctect.review.entity.ReviewTag;
import dac2dac.doctect.review.repository.ReviewRepository;
import dac2dac.doctect.review.repository.ReviewReviewTagRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoncontactDiagService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentTagRepository departmentTagRepository;
    private final DoctorRepository doctorRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReviewTagRepository reviewReviewTagRepository;

    public DepartmentListDto getDepartmentList() {
        List<DepartmentInfo> departmentInfoList = departmentRepository.findAll().stream()
            .map(department -> {
                List<String> tags = departmentTagRepository.findByDepartmentId(department.getId()).stream()
                    .map(departmentTag -> departmentTag.getTag())
                    .collect(Collectors.toList());

                return DepartmentInfo.builder()
                    .departmentId(department.getId())
                    .departmentName(department.getDepartmentName())
                    .tags(tags)
                    .build();
            })
            .sorted(Comparator.comparing(DepartmentInfo::getDepartmentName))
            .collect(Collectors.toList());

        return DepartmentListDto.builder()
            .cnt(departmentInfoList.size())
            .departmentInfoList(departmentInfoList)
            .build();
    }

    public DoctorListDto getDoctorList(Long departmentId) {
        List<DoctorItem> doctorItemList = doctorRepository.findByDepartmentId(departmentId).stream()
            .map(doctor -> {
                Integer reviewCnt = reviewRepository.findByDoctorId(doctor.getId()).size();

                return DoctorItem.builder()
                    .id(doctor.getId())
                    .name(doctor.getName())
                    .hospitalName(doctor.getHospital().getName())
                    .profilePath(doctor.getProfileImagePath())
                    .averageRating(doctor.getAvarageRating())
                    .reviewCnt(reviewCnt)
                    .isOpen(isAgencyOpenNow(doctor.getDiagTime()))
                    .todayOpenTime(findTodayOpenTime(doctor.getDiagTime()))
                    .todayCloseTime(findTodayCloseTime(doctor.getDiagTime()))
                    .build();
            })
            .collect(Collectors.toList());

        return DoctorListDto.builder()
            .cnt(doctorItemList.size())
            .doctorItemList(doctorItemList)
            .build();
    }

    public DoctorDto getDoctorDetailInfo(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.DOCTOR_NOT_FOUND));

        List<Review> findDoctorReviews = reviewRepository.findByDoctorId(doctorId);

        List<DoctorReview> doctorReviews = findDoctorReviews.stream()
            .map(r -> {
                List<String> reviewTagList = reviewReviewTagRepository.findReviewTagNameByReviewId(r.getId());

                return DoctorReview.builder()
                    .reviewTagList(reviewTagList)
                    .createdAt(r.getCreateDate())
                    .rating(r.getRating())
                    .contents(r.getContent())
                    .build();
            })
            .sorted(Comparator.comparing(DoctorReview::getCreatedAt).reversed())
            .collect(Collectors.toList());

        DoctorInfo doctorInfo = DoctorInfo.builder()
            .name(doctor.getName())
            .hospitalName(doctor.getHospital().getName())
            .profilePath(doctor.getProfileImagePath())
            .averageRating(doctor.getAvarageRating())
            .reviewCnt(doctorReviews.size())
            .isOpen(isAgencyOpenNow(doctor.getDiagTime()))
            .todayOpenTime(findTodayOpenTime(doctor.getDiagTime()))
            .todayCloseTime(findTodayCloseTime(doctor.getDiagTime()))
            .top3ReviewTagList(calTop3ReviewTag(findDoctorReviews))
            .build();

        DoctorIntroduction doctorIntroduction = DoctorIntroduction.builder()
            .department(doctor.getDepartment().getDepartmentName())
            .diagTime(doctor.getDiagTime())
            .oneLiner(doctor.getOneLiner())
            .experience(doctor.getExperience())
            .hospitalId(doctor.getHospital().getId())
            .hospitalName(doctor.getHospital().getName())
            .hospitalAddress(doctor.getHospital().getAddress())
            .hospitalThumnail(doctor.getHospital().getThumnail())
            .build();

        DoctorReviewList doctorReviewList = DoctorReviewList.builder()
            .cnt(doctorReviews.size())
            .doctorReviewList(doctorReviews)
            .build();

        return DoctorDto.builder()
            .doctorInfo(doctorInfo)
            .doctorIntroduction(doctorIntroduction)
            .doctorReviewList(doctorReviewList)
            .build();
    }

    public List<Top3ReviewTagInfo> calTop3ReviewTag(List<Review> findDoctorReviews) {
        // 리뷰 ID를 기반으로 리뷰 태그들을 추출
        List<ReviewTag> reviewTagList = findDoctorReviews.stream()
            .flatMap(r -> reviewReviewTagRepository.findReviewTagByReviewId(r.getId()).stream())
            .filter(rt -> rt.getIsPositive())
            .collect(Collectors.toList());

        // 각 리뷰 태그별로 개수 집계
        Map<String, Long> tagCountMap = reviewTagList.stream()
            .collect(Collectors.groupingBy(tag -> tag.getReviewTagName(), Collectors.counting()));

        // 전체 리뷰 태그 수 계산
        long totalReviews = findDoctorReviews.size();

        // 각 리뷰 태그의 비율 계산 및 Top3ReviewTagInfo 객체 생성
        List<Top3ReviewTagInfo> tagInfos = tagCountMap.entrySet().stream()
            .map(entry -> {
                String tagName = entry.getKey();
                long count = entry.getValue();
                double percentage = (double) count / totalReviews * 100;

                return Top3ReviewTagInfo.builder()
                    .reviewTagName(tagName)
                    .percentage(percentage)
                    .cnt((int) count)
                    .build();
            })
            .sorted(Comparator.comparing(Top3ReviewTagInfo::getCnt).reversed())
            .limit(3)
            .collect(Collectors.toList());

        return tagInfos;
    }
}
