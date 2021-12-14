import { CloseSvg, FormComposer, Header } from "@egovernments/digit-ui-react-components";
import React, { Fragment, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import CreateNewSurvey from "../../../components/Surveys/SurveyForms";

export const answerTypeEnum = {
  "Short Answer": "SHORT_ANSWER_TYPE",
  Paragraph: "LONG_ANSWER_TYPE",
  "Multiple Choice": "MULTIPLE_ANSWER_TYPE",
  "Check Boxes": "CHECKBOX_ANSWER_TYPE",
  Date: "DATE_ANSWER_TYPE",
  Time: "TIME_ANSWER_TYPE",
};


/**TODO NRJ-egov handle this by setting correct state inside the surveyFormMaker */
export const mapQuestions = (questions =[]) =>{
  if(!questions.length) return;
  return questions.map(({formConfig},index)=>{
      const {options:choices, questionStatement,required, type:stringType} = formConfig;

      const finalQuestion = {questionStatement, required, type:answerTypeEnum[stringType]};
      if(stringType === "Multiple Choice" || stringType ==="Check Boxes") {
        finalQuestion["options"] = choices;
      }
      return finalQuestion;
    })
}

const NewSurveys = () => {
  const { t } = useTranslation();
  const history = useHistory();
  
  const onSubmit = (data) => {
    const { collectCitizenInfo, title, description, tenantIds, fromDate, toDate, fromTime, toTime, questions } = data;
    const mappedQuestions = mapQuestions(questions);
    const details = {
      SurveyEntity: {
        tenantIds: tenantIds.map(({code})=>(code)),
        title,
        description,
        collectCitizenInfo: collectCitizenInfo.code,
        startDate: new Date(`${fromDate} ${fromTime}`).getTime(),
        endDate: new Date(`${toDate} ${toTime}`).getTime(),
        questions:mappedQuestions
      },
    };
    //console.log("what the data is >>", { tenantIds, questions, details });
    history.push("/digit-ui/employee/engagement/surveys/create-response", details)
  };

  const defaultValues = {
    fromDate: "",
    fromTime: "",
    toDate: "",
    toTime: "",
    questions: {},
    collectCitizenInfo: "",
    tenantIds:[]
  };

  return (
    <Fragment>
      <Header>{t("CS_COMMON_SURVEYS")}</Header>
      <CreateNewSurvey t={t} onSubmit={onSubmit} initialFormValues={defaultValues} />
    </Fragment>
  );
};

export default NewSurveys;
