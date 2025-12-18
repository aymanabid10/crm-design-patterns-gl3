import axios from "axios";

const baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const apiClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

// Lead interfaces
export interface LeadData {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  jobTitle?: string;
  status: "NEW" | "CONTACTED" | "QUALIFIED" | "UNQUALIFIED" | "CONVERTED";
  source?: string;
  score?: number;
  assignedTo: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateLeadData {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  jobTitle?: string;
  source?: string;
  notes?: string;
  assignedTo: string;
}

// Contact interfaces
export interface ContactData {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  jobTitle?: string;
  type: "CUSTOMER" | "PARTNER" | "VENDOR" | "OTHER";
  status: "ACTIVE" | "INACTIVE";
  createdAt: string;
  updatedAt: string;
}

export interface CreateContactData {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  jobTitle?: string;
  type: "CUSTOMER" | "PARTNER" | "VENDOR" | "OTHER";
  assignedTo: string;
}

// Lead API
export const leadService = {
  fetchAll: () => apiClient.get<LeadData[]>("/api/leads"),
  fetchById: (id: number) => apiClient.get<LeadData>(`/api/leads/${id}`),
  fetchByStatus: (status: string) =>
    apiClient.get<LeadData[]>(`/api/leads/status/${status}`),
  fetchStats: () =>
    apiClient.get<Record<string, number>>("/api/leads/stats/count"),
  fetchDuplicates: () => apiClient.get<LeadData[][]>("/api/leads/duplicates"),
  fetchAssigned: (userId: string) =>
    apiClient.get<LeadData[]>(`/api/leads/assigned/${userId}`),
  create: (data: CreateLeadData) =>
    apiClient.post<LeadData>("/api/leads", data),
  update: (id: number, data: Partial<CreateLeadData>) =>
    apiClient.put<LeadData>(`/api/leads/${id}`, data),
  qualifyLead: (id: number, score: number) =>
    apiClient.post<LeadData>(`/api/leads/${id}/qualify?score=${score}`),
  disqualifyLead: (id: number) =>
    apiClient.post<LeadData>(`/api/leads/${id}/disqualify`),
  convertLead: (id: number) => apiClient.post(`/api/leads/${id}/convert`),
  markAsContacted: (id: number) =>
    apiClient.post<LeadData>(`/api/leads/${id}/contact`),
  mergeLeads: (sourceId: number, targetId: number) =>
    apiClient.post(
      `/api/leads/merge?sourceId=${sourceId}&targetId=${targetId}`
    ),
  remove: (id: number) => apiClient.delete(`/api/leads/${id}`),
};

// Contact API
export const contactService = {
  fetchAll: () => apiClient.get<ContactData[]>("/api/contacts"),
  fetchById: (id: number) => apiClient.get<ContactData>(`/api/contacts/${id}`),
  fetchByType: (type: string) =>
    apiClient.get<ContactData[]>(`/api/contacts/type/${type}`),
  create: (data: CreateContactData) =>
    apiClient.post<ContactData>("/api/contacts", data),
  update: (id: number, data: Partial<CreateContactData>) =>
    apiClient.put<ContactData>(`/api/contacts/${id}`, data),
  remove: (id: number) => apiClient.delete(`/api/contacts/${id}`),
};
