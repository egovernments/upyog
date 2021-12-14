import { useQuery } from "react-query"
import useEmpBPAREGSearch from "./useEmpBPAREGSearch";
import useBPASearch from "./useBPASearch";

const useOBPSSearch = (selectedType, payload, tenantId, filters, params, config = {}) => {
    if((selectedType && selectedType.includes("STAKEHOLDER")) || (Object.keys(payload).length>0 && payload?.applicationType && payload?.applicationType.includes("STAKEHOLDER")))
    {
        return useEmpBPAREGSearch(tenantId, {}, params);
    }
    else
    {
        return useBPASearch(tenantId, filters);
    }
}

export default useOBPSSearch;