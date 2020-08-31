package br.edu.utfpr.dv.siacoes.model;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.util.DateUtils;

public @Data class ActivitySubmissionReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private String student;
	private String studentCode;
	private int registerSemester;
	private int registerYear;
	private double totalScore;
	private String situation;
	private List<ActivitySubmissionDetailReport> details;
	private List<ActivitySubmissionFooterReport> footer;
}