import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { leadService, type CreateLeadData } from "../services/api";
import { useState } from "react";

const getStatusColor = (status: string) => {
  const colors = {
    NEW: "bg-blue-50 text-blue-700 ring-1 ring-blue-600/20",
    CONTACTED: "bg-yellow-50 text-yellow-700 ring-1 ring-yellow-600/20",
    QUALIFIED: "bg-green-50 text-green-700 ring-1 ring-green-600/20",
    UNQUALIFIED: "bg-red-50 text-red-700 ring-1 ring-red-600/20",
    CONVERTED: "bg-purple-50 text-purple-700 ring-1 ring-purple-600/20",
  };
  return (
    colors[status as keyof typeof colors] ||
    "bg-gray-50 text-gray-700 ring-1 ring-gray-600/20"
  );
};

export default function LeadsPage() {
  const queryClient = useQueryClient();
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedLead, setSelectedLead] = useState<any>(null);
  const [statusFilter, setStatusFilter] = useState<string>("ALL");
  const [, setShowDuplicatesModal] = useState(false);
  const [, setDuplicates] = useState<any[][]>([]);

  const {
    data: leads,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["leads", statusFilter],
    queryFn: async () => {
      if (statusFilter === "ALL") {
        const response = await leadService.fetchAll();
        return response.data;
      } else {
        const response = await leadService.fetchByStatus(statusFilter);
        return response.data;
      }
    },
  });

  const stats = {
    total: leads?.length || 0,
    new: leads?.filter((l) => l.status === "NEW").length || 0,
    qualified: leads?.filter((l) => l.status === "QUALIFIED").length || 0,
    converted: leads?.filter((l) => l.status === "CONVERTED").length || 0,
  };

  const removeMutation = useMutation({
    mutationFn: (id: number) => leadService.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
  });

  const qualifyMutation = useMutation({
    mutationFn: ({ id, score }: { id: number; score: number }) =>
      leadService.qualifyLead(id, score),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
  });

  const convertMutation = useMutation({
    mutationFn: (id: number) => leadService.convertLead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
      queryClient.invalidateQueries({ queryKey: ["contacts"] });
    },
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateLeadData) => leadService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
      setShowAddModal(false);
    },
  });

  const disqualifyMutation = useMutation({
    mutationFn: (id: number) => leadService.disqualifyLead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
  });

  const contactMutation = useMutation({
    mutationFn: (id: number) => leadService.markAsContacted(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
    },
  });


  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<CreateLeadData> }) =>
      leadService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["leads"] });
      setShowEditModal(false);
      setSelectedLead(null);
    },
  });

  const handleCreateLead = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const data: CreateLeadData = {
      firstName: formData.get("firstName") as string,
      lastName: formData.get("lastName") as string,
      email: formData.get("email") as string,
      phone: (formData.get("phone") as string) || undefined,
      company: (formData.get("company") as string) || undefined,
      jobTitle: (formData.get("jobTitle") as string) || undefined,
      source: (formData.get("source") as string) || undefined,
      notes: (formData.get("notes") as string) || undefined,
      assignedTo: formData.get("assignedTo") as string,
    };
    createMutation.mutate(data);
  };

  const handleEditLead = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!selectedLead) return;
    const formData = new FormData(e.currentTarget);
    const data: Partial<CreateLeadData> = {
      firstName: formData.get("firstName") as string,
      lastName: formData.get("lastName") as string,
      phone: (formData.get("phone") as string) || undefined,
      company: (formData.get("company") as string) || undefined,
      jobTitle: (formData.get("jobTitle") as string) || undefined,
      notes: (formData.get("notes") as string) || undefined,
    };
    updateMutation.mutate({ id: selectedLead.id, data });
  };

  const handleFindDuplicates = async () => {
    try {
      const response = await leadService.fetchDuplicates();
      setDuplicates(response.data);
      setShowDuplicatesModal(true);
    } catch (error) {
      console.error("Error fetching duplicates:", error);
      alert("Failed to fetch duplicates");
    }
  };

  if (isLoading) {
    return (
      <div className="flex flex-col justify-center items-center h-96">
        <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-indigo-600"></div>
        <p className="mt-4 text-gray-600 font-medium">Loading leads...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mx-4">
        <div className="bg-red-50 border-l-4 border-red-400 p-6 rounded-lg shadow-sm">
          <div className="flex items-start">
            <div className="flex-shrink-0">
              <svg
                className="h-6 w-6 text-red-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
            <div className="ml-3">
              <h3 className="text-lg font-medium text-red-800">
                Error loading leads
              </h3>
              <p className="mt-2 text-sm text-red-700">
                Please check if the backend server is running on port 8080.
              </p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      {/* Add Lead Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity z-50">
          <div className="fixed inset-0 z-50 overflow-y-auto">
            <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
              <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
                <form onSubmit={handleCreateLead}>
                  <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-900">
                        Add New Lead
                      </h3>
                      <button
                        type="button"
                        onClick={() => setShowAddModal(false)}
                        className="text-gray-400 hover:text-gray-500"
                      >
                        <svg
                          className="h-6 w-6"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M6 18L18 6M6 6l12 12"
                          />
                        </svg>
                      </button>
                    </div>
                    <div className="space-y-4">
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            First Name *
                          </label>
                          <input
                            type="text"
                            name="firstName"
                            required
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Last Name *
                          </label>
                          <input
                            type="text"
                            name="lastName"
                            required
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Email *
                        </label>
                        <input
                          type="email"
                          name="email"
                          required
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Phone
                        </label>
                        <input
                          type="tel"
                          name="phone"
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Company
                          </label>
                          <input
                            type="text"
                            name="company"
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Job Title
                          </label>
                          <input
                            type="text"
                            name="jobTitle"
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Source
                        </label>
                        <select
                          name="source"
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        >
                          <option value="">Select source</option>
                          <option value="WEBSITE">Website</option>
                          <option value="REFERRAL">Referral</option>
                          <option value="SOCIAL_MEDIA">Social Media</option>
                          <option value="EMAIL_CAMPAIGN">Email Campaign</option>
                          <option value="PHONE_CALL">Phone Call</option>
                          <option value="TRADE_SHOW">Trade Show</option>
                          <option value="PARTNER">Partner</option>
                          <option value="OTHER">Other</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Assigned To *
                        </label>
                        <input
                          type="text"
                          name="assignedTo"
                          required
                          placeholder="e.g., john.doe"
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Notes
                        </label>
                        <textarea
                          name="notes"
                          rows={3}
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                    </div>
                  </div>
                  <div className="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                    <button
                      type="submit"
                      disabled={createMutation.isPending}
                      className="inline-flex w-full justify-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 sm:ml-3 sm:w-auto disabled:opacity-50"
                    >
                      {createMutation.isPending ? "Creating..." : "Create Lead"}
                    </button>
                    <button
                      type="button"
                      onClick={() => setShowAddModal(false)}
                      className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Edit Lead Modal */}
      {showEditModal && selectedLead && (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity z-50">
          <div className="fixed inset-0 z-50 overflow-y-auto">
            <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
              <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
                <form onSubmit={handleEditLead}>
                  <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-900">
                        Edit Lead
                      </h3>
                      <button
                        type="button"
                        onClick={() => {
                          setShowEditModal(false);
                          setSelectedLead(null);
                        }}
                        className="text-gray-400 hover:text-gray-500"
                      >
                        <svg
                          className="h-6 w-6"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                        >
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M6 18L18 6M6 6l12 12"
                          />
                        </svg>
                      </button>
                    </div>
                    <div className="space-y-4">
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            First Name *
                          </label>
                          <input
                            type="text"
                            name="firstName"
                            required
                            defaultValue={selectedLead.firstName}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Last Name *
                          </label>
                          <input
                            type="text"
                            name="lastName"
                            required
                            defaultValue={selectedLead.lastName}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Phone
                        </label>
                        <input
                          type="tel"
                          name="phone"
                          defaultValue={selectedLead.phone || ""}
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Company
                          </label>
                          <input
                            type="text"
                            name="company"
                            defaultValue={selectedLead.company || ""}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700">
                            Job Title
                          </label>
                          <input
                            type="text"
                            name="jobTitle"
                            defaultValue={selectedLead.jobTitle || ""}
                            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                          />
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Notes
                        </label>
                        <textarea
                          name="notes"
                          rows={3}
                          defaultValue={selectedLead.notes || ""}
                          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
                        />
                      </div>
                    </div>
                  </div>
                  <div className="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                    <button
                      type="submit"
                      disabled={updateMutation.isPending}
                      className="inline-flex w-full justify-center rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 sm:ml-3 sm:w-auto disabled:opacity-50"
                    >
                      {updateMutation.isPending ? "Updating..." : "Update Lead"}
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setShowEditModal(false);
                        setSelectedLead(null);
                      }}
                      className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
        <div className="px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="sm:flex sm:items-center sm:justify-between mb-8">
            <div>
              <h1 className="text-4xl font-extrabold bg-gradient-to-r from-indigo-600 to-blue-600 bg-clip-text text-transparent">
                Leads Management
              </h1>
              <p className="mt-3 text-base text-gray-600 flex items-center gap-2">
                <svg
                  className="w-5 h-5 text-indigo-500"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M13 10V3L4 14h7v7l9-11h-7z"
                  />
                </svg>
                Track and manage your sales pipeline
              </p>
            </div>
            <div className="mt-6 sm:mt-0 flex flex-wrap gap-3 items-center">
              <div className="relative">
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="appearance-none bg-white border border-gray-300 rounded-xl pl-4 pr-10 py-2.5 text-sm font-medium text-gray-700 shadow-sm hover:border-indigo-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                >
                  <option value="ALL">📊 All Status</option>
                  <option value="NEW">🆕 New</option>
                  <option value="CONTACTED">📞 Contacted</option>
                  <option value="QUALIFIED">✅ Qualified</option>
                  <option value="UNQUALIFIED">❌ Unqualified</option>
                  <option value="CONVERTED">🎉 Converted</option>
                </select>
                <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                  <svg
                    className="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 9l-7 7-7-7"
                    />
                  </svg>
                </div>
              </div>
              <button
                onClick={handleFindDuplicates}
                type="button"
                className="inline-flex items-center gap-x-2 rounded-xl bg-gradient-to-r from-orange-500 to-amber-500 px-5 py-2.5 text-sm font-semibold text-white shadow-lg hover:shadow-xl hover:scale-105 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-orange-600 transition-all duration-200"
              >
                <svg
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
                  />
                </svg>
                Find Duplicates
              </button>
              <button
                onClick={() => setShowAddModal(true)}
                type="button"
                className="inline-flex items-center gap-x-2 rounded-xl bg-gradient-to-r from-indigo-600 to-blue-600 px-6 py-2.5 text-sm font-semibold text-white shadow-lg hover:shadow-xl hover:scale-105 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 transition-all duration-200"
              >
                <svg
                  className="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 4v16m8-8H4"
                  />
                </svg>
                Add Lead
              </button>
            </div>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="px-4 sm:px-6 lg:px-8 mt-8">
          <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
            <div className="relative bg-white overflow-hidden rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 border border-blue-100">
              <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-blue-400 to-blue-600 rounded-bl-full opacity-10"></div>
              <div className="p-6 relative">
                <div className="flex items-center gap-4">
                  <div className="flex-shrink-0 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl p-4 shadow-lg">
                    <svg
                      className="h-7 w-7 text-white"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                      />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <dl>
                      <dt className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                        Total Leads
                      </dt>
                      <dd className="mt-2 text-3xl font-extrabold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                        {stats.total}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
            <div className="relative bg-white overflow-hidden rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 border border-yellow-100">
              <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-yellow-400 to-amber-600 rounded-bl-full opacity-10"></div>
              <div className="p-6 relative">
                <div className="flex items-center gap-4">
                  <div className="flex-shrink-0 bg-gradient-to-br from-yellow-500 to-amber-600 rounded-xl p-4 shadow-lg">
                    <svg
                      className="h-7 w-7 text-white"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                      />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <dl>
                      <dt className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                        New
                      </dt>
                      <dd className="mt-2 text-3xl font-extrabold bg-gradient-to-r from-yellow-600 to-amber-600 bg-clip-text text-transparent">
                        {stats.new}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
            <div className="relative bg-white overflow-hidden rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 border border-green-100">
              <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-green-400 to-emerald-600 rounded-bl-full opacity-10"></div>
              <div className="p-6 relative">
                <div className="flex items-center gap-4">
                  <div className="flex-shrink-0 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl p-4 shadow-lg">
                    <svg
                      className="h-7 w-7 text-white"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                      />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <dl>
                      <dt className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                        Qualified
                      </dt>
                      <dd className="mt-2 text-3xl font-extrabold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
                        {stats.qualified}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
            <div className="relative bg-white overflow-hidden rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 border border-purple-100">
              <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-purple-400 to-pink-600 rounded-bl-full opacity-10"></div>
              <div className="p-6 relative">
                <div className="flex items-center gap-4">
                  <div className="flex-shrink-0 bg-gradient-to-br from-purple-500 to-pink-600 rounded-xl p-4 shadow-lg">
                    <svg
                      className="h-7 w-7 text-white"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M13 10V3L4 14h7v7l9-11h-7z"
                      />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <dl>
                      <dt className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                        Converted
                      </dt>
                      <dd className="mt-2 text-3xl font-extrabold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                        {stats.converted}
                      </dd>
                    </dl>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Table */}
        <div className="px-4 sm:px-6 lg:px-8 mt-10">
          <div className="bg-white shadow-xl border border-gray-100 rounded-2xl overflow-hidden backdrop-blur-sm">
            <div className="overflow-x-auto">
              {leads && leads.length > 0 ? (
                <div>
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gradient-to-r from-gray-50 to-gray-100">
                      <tr>
                        <th className="py-4 pl-6 pr-3 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Name
                        </th>
                        <th className="px-3 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Email
                        </th>
                        <th className="px-3 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Company
                        </th>
                        <th className="px-3 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Status
                        </th>
                        <th className="px-3 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Score
                        </th>
                        <th className="px-3 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">
                          Assigned To
                        </th>
                        <th className="relative py-4 pl-3 pr-6">
                          <span className="sr-only">Actions</span>
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100 bg-white">
                      {leads.map((lead) => (
                        <tr
                          key={lead.id}
                          className="hover:bg-gradient-to-r hover:from-indigo-50 hover:to-blue-50 transition-all duration-200"
                        >
                          <td className="whitespace-nowrap py-5 pl-6 pr-3 text-sm font-semibold text-gray-900">
                            {lead.firstName} {lead.lastName}
                          </td>
                          <td className="whitespace-nowrap px-3 py-5 text-sm text-gray-600">
                            {lead.email}
                          </td>
                          <td className="whitespace-nowrap px-3 py-5 text-sm text-gray-600">
                            {lead.company || "-"}
                          </td>
                          <td className="whitespace-nowrap px-3 py-5 text-sm">
                            <span
                              className={`inline-flex rounded-full px-3 py-1 text-xs font-bold shadow-sm ${getStatusColor(
                                lead.status
                              )}`}
                            >
                              {lead.status}
                            </span>
                          </td>
                          <td className="whitespace-nowrap px-3 py-5 text-sm">
                            <span className="inline-flex items-center px-2.5 py-1 rounded-lg bg-indigo-100 text-indigo-800 font-semibold">
                              {lead.score || "-"}
                            </span>
                          </td>
                          <td className="whitespace-nowrap px-3 py-5 text-sm">
                            <span className="inline-flex items-center px-3 py-1 rounded-lg bg-gray-100 text-gray-700 font-medium">
                              {lead.assignedTo}
                            </span>
                          </td>
                          <td className="relative whitespace-nowrap py-5 pl-3 pr-6 text-right text-sm font-medium">
                            <div className="flex gap-2 justify-end flex-wrap">
                              <button
                                onClick={() => {
                                  setSelectedLead(lead);
                                  setShowEditModal(true);
                                }}
                                className="inline-flex items-center px-3 py-1.5 border-2 border-indigo-300 text-xs font-semibold rounded-lg text-indigo-700 bg-indigo-50 hover:bg-indigo-100 hover:border-indigo-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all shadow-sm hover:shadow"
                              >
                                <svg
                                  className="w-3.5 h-3.5 mr-1"
                                  fill="none"
                                  viewBox="0 0 24 24"
                                  stroke="currentColor"
                                >
                                  <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                                  />
                                </svg>
                                Edit
                              </button>
                              {lead.status === "NEW" && (
                                <>
                                  <button
                                    onClick={() =>
                                      contactMutation.mutate(lead.id)
                                    }
                                    className="inline-flex items-center px-3 py-1.5 border-2 border-yellow-300 text-xs font-semibold rounded-lg text-yellow-700 bg-yellow-50 hover:bg-yellow-100 hover:border-yellow-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500 transition-all shadow-sm hover:shadow disabled:opacity-50"
                                    disabled={contactMutation.isPending}
                                  >
                                    <svg
                                      className="w-3.5 h-3.5 mr-1"
                                      fill="none"
                                      viewBox="0 0 24 24"
                                      stroke="currentColor"
                                    >
                                      <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                                      />
                                    </svg>
                                    {contactMutation.isPending
                                      ? "Contacting..."
                                      : "Contact"}
                                  </button>
                                  <button
                                    onClick={() =>
                                      qualifyMutation.mutate({
                                        id: lead.id,
                                        score: 75,
                                      })
                                    }
                                    className="inline-flex items-center px-3 py-1.5 border-2 border-green-300 text-xs font-semibold rounded-lg text-green-700 bg-green-50 hover:bg-green-100 hover:border-green-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-all shadow-sm hover:shadow disabled:opacity-50"
                                    disabled={qualifyMutation.isPending}
                                  >
                                    <svg
                                      className="w-3.5 h-3.5 mr-1"
                                      fill="none"
                                      viewBox="0 0 24 24"
                                      stroke="currentColor"
                                    >
                                      <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                                      />
                                    </svg>
                                    {qualifyMutation.isPending
                                      ? "Processing..."
                                      : "Qualify"}
                                  </button>
                                  <button
                                    onClick={() =>
                                      disqualifyMutation.mutate(lead.id)
                                    }
                                    className="inline-flex items-center px-3 py-1.5 border-2 border-red-300 text-xs font-semibold rounded-lg text-red-700 bg-red-50 hover:bg-red-100 hover:border-red-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all shadow-sm hover:shadow disabled:opacity-50"
                                    disabled={disqualifyMutation.isPending}
                                  >
                                    <svg
                                      className="w-3.5 h-3.5 mr-1"
                                      fill="none"
                                      viewBox="0 0 24 24"
                                      stroke="currentColor"
                                    >
                                      <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M6 18L18 6M6 6l12 12"
                                      />
                                    </svg>
                                    {disqualifyMutation.isPending
                                      ? "Processing..."
                                      : "Disqualify"}
                                  </button>
                                </>
                              )}
                              {lead.status === "QUALIFIED" && (
                                <button
                                  onClick={() =>
                                    convertMutation.mutate(lead.id)
                                  }
                                  className="inline-flex items-center px-3 py-1.5 border-2 border-purple-300 text-xs font-semibold rounded-lg text-purple-700 bg-purple-50 hover:bg-purple-100 hover:border-purple-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 transition-all shadow-sm hover:shadow disabled:opacity-50"
                                  disabled={convertMutation.isPending}
                                >
                                  <svg
                                    className="w-3.5 h-3.5 mr-1"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                  >
                                    <path
                                      strokeLinecap="round"
                                      strokeLinejoin="round"
                                      strokeWidth={2}
                                      d="M13 10V3L4 14h7v7l9-11h-7z"
                                    />
                                  </svg>
                                  {convertMutation.isPending
                                    ? "Converting..."
                                    : "Convert"}
                                </button>
                              )}
                              <button
                                onClick={() => {
                                  if (
                                    confirm(
                                      "Are you sure you want to delete this lead?"
                                    )
                                  ) {
                                    removeMutation.mutate(lead.id);
                                  }
                                }}
                                className="inline-flex items-center px-3 py-1.5 border-2 border-red-400 text-xs font-semibold rounded-lg text-red-700 bg-red-50 hover:bg-red-600 hover:text-white hover:border-red-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-all shadow-sm hover:shadow disabled:opacity-50"
                                disabled={removeMutation.isPending}
                              >
                                <svg
                                  className="w-3.5 h-3.5 mr-1"
                                  fill="none"
                                  viewBox="0 0 24 24"
                                  stroke="currentColor"
                                >
                                  <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                                  />
                                </svg>
                                {removeMutation.isPending
                                  ? "Deleting..."
                                  : "Delete"}
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="text-center py-16 px-6">
                  <svg
                    className="mx-auto h-16 w-16 text-gray-400"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={1.5}
                      d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                    />
                  </svg>
                  <h3 className="mt-4 text-lg font-medium text-gray-900">
                    No leads found
                  </h3>
                  <p className="mt-2 text-sm text-gray-500">
                    Get started by creating a new lead.
                  </p>
                  <div className="mt-6">
                    <button
                      onClick={() => setShowAddModal(true)}
                      type="button"
                      className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                    >
                      <svg
                        className="-ml-1 mr-2 h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M12 4v16m8-8H4"
                        />
                      </svg>
                      Add Lead
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
