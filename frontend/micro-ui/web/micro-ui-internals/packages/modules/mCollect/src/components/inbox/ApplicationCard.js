import React, { useCallback, useEffect, useState } from "react";

import { Card, DetailsCard, Loader, PopUp, SearchAction } from "@upyog/digit-ui-react-components";
import { FilterAction } from "@upyog/digit-ui-react-components";
import SearchApplication from "./search";
import SortBy from "./SortBy";

export const ApplicationCard = ({
  t,
  data,
  defaultSearchParams={},
  onFilterChange,
  onSearch,
  onSort,
  serviceRequestIdKey,
  isFstpOperator,
  isLoading,
  isSearch,
  searchParams,
  searchFields,
  sortParams,
  linkPrefix,
  removeParam,
  filterComponent,
}) => {
  const [type, setType] = useState(isSearch ? "SEARCH" : "");
  const [popup, setPopup] = useState(isSearch ? true : false);
  const [_sortparams, setSortParams] = useState(sortParams);
  const [FilterComp] = useState(() => Digit.ComponentRegistryService?.getComponent(filterComponent));
  const [searchFilterParams, setSearchFilterParams] = useState(searchParams);

  const onSearchFilter = (params) => {
    onFilterChange(params, true);
    setPopup(false);
  };

  useEffect(() => {
    if (type) setPopup(true);
  }, [type]);

  const handlePopupClose = () => {
    setPopup(false);
    setType("");
    setSortParams(sortParams);
  };

  if (isLoading) {
    return <Loader />;
  }

  let result;
  if (!data || data?.length === 0) {
    result = (
      <Card style={{ marginTop: 20 }}>
        {t("CS_MYAPPLICATIONS_NO_APPLICATION")
          .split("\\n")
          .map((text, index) => (
            <p key={index} style={{ textAlign: "center" }}>
              {text}
            </p>
          ))}
      </Card>
    );
  } else if (data && data?.length > 0) {
    result = <DetailsCard data={data} serviceRequestIdKey={serviceRequestIdKey} linkPrefix={linkPrefix ? linkPrefix : "/digit-ui/employee/mcollect/challansearch/"} />;
  }

  return (
    <React.Fragment>
      <div className="searchBox">
        {onSearch && (
          <SearchAction
            text="SEARCH"
            handleActionClick={() => {
              setType("SEARCH");
              setSearchFilterParams({
                businessService: [],
                status: []
              });
              setPopup(true);
            }}
          />
        )}
        {!isSearch && onFilterChange && (
          <FilterAction
            text="FILTER"
            handleActionClick={() => {
              setType("FILTER");
              setSearchFilterParams({
                businessService: [],
                status: []
              });
              setPopup(true);
            }}
          />
        )}
        {/* <FilterAction
          text="SORT"
          handleActionClick={() => {
            setType("SORT");
            setPopup(true);
          }}
        /> */}
      </div>
      {result}
      {popup && (
        <PopUp>
          {type === "FILTER" && (
            <div className="popup-module">
              {<FilterComp onFilterChange={onSearchFilter} onRefresh={onFilterChange} Close={handlePopupClose} type="mobile" searchParams={searchFilterParams} defaultSearchParams={defaultSearchParams} />}
            </div>
          )}
          {/* {type === "SORT" && (
            <div className="popup-module">
              {<SortBy type="mobile" sortParams={sortParams} onClose={handlePopupClose} type="mobile" onSort={onSort} />}
            </div>
          )} */}
          {type === "SEARCH" && (
            <div className="popup-module">
              <SearchApplication
                type="mobile"
                onClose={handlePopupClose}
                onSearch={onSearch}
                isFstpOperator={isFstpOperator}
                searchParams={searchFilterParams}
                searchFields={searchFields}
              />
            </div>
          )}
        </PopUp>
      )}
    </React.Fragment>
  );
};
