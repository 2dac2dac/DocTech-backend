package dac2dac.doctect.agency.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "약국", description = "Pharmacy API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PharmacyController {

}
