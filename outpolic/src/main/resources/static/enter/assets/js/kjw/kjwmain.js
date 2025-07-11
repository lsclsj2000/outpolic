document.addEventListener('DOMContentLoaded', function () {
    const MAX_FILE_COUNT = 5; // 최대 파일 개수

    /**
     * 파일 입력 필드에 대한 미리보기 기능을 초기화합니다.
     * @param {string} fileInputId - 파일 입력 필드의 ID
     * @param {string} previewContainerId - 미리보기 이미지를 표시할 컨테이너의 ID
     */
    function initializeFilePreview(fileInputId, previewContainerId) {
        const fileInput = document.getElementById(fileInputId);
        const previewContainer = document.getElementById(previewContainerId);

        if (!fileInput || !previewContainer) {
            console.error(`Element with ID '${fileInputId}' or '${previewContainerId}' not found.`);
            return;
        }

        fileInput.addEventListener('change', function (event) {
            previewContainer.innerHTML = ''; // 이전 미리보기 제거
            const files = event.target.files;
            const currentFiles = new DataTransfer(); // 새로운 DataTransfer 객체를 생성하여 유효한 파일만 담습니다.

            if (files.length > MAX_FILE_COUNT) {
                alert(`파일은 최대 ${MAX_FILE_COUNT}개까지 첨부할 수 있습니다.`);
                fileInput.value = ''; // 선택된 파일 초기화
                return;
            }

            for (let i = 0; i < files.length; i++) {
                const file = files.item(i);
                if (file) {
                    currentFiles.items.add(file); // 유효한 파일만 추가
                    const fileType = file.type;
                    const fileName = file.name;

                    const fileDiv = document.createElement('div');
                    fileDiv.classList.add('relative', 'w-24', 'h-24', 'overflow-hidden', 'rounded-lg', 'shadow-sm', 'group', 'flex', 'items-center', 'justify-center', 'text-gray-500', 'bg-gray-100', 'text-center', 'break-all', 'p-1');

                    if (fileType.startsWith('image/')) {
                        const reader = new FileReader();
                        reader.onload = (e) => {
                            fileDiv.innerHTML = `
                                <img src="${e.target.result}" alt="Reference File" class="w-full h-full object-cover">
                                <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-file-btn" data-file-name="${fileName}">
                                    <i class="fas fa-times-circle"></i>
                                </button>
                            `;
                        };
                        reader.readAsDataURL(file);
                    } else {
                        let iconClass = 'fas fa-file';
                        if (fileType.includes('pdf')) iconClass = 'fas fa-file-pdf';
                        else if (fileType.includes('word')) iconClass = 'fas fa-file-word';
                        else if (fileType.includes('powerpoint')) iconClass = 'fas fa-file-powerpoint';
                        else if (fileType.includes('text')) iconClass = 'fas fa-file-alt';

                        fileDiv.innerHTML = `
                            <div class="flex flex-col items-center justify-center p-1">
                                <i class="${iconClass} text-3xl mb-1 text-gray-400"></i>
                                <span class="text-xs font-medium">${fileName}</span>
                            </div>
                            <button type="button" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 text-white opacity-0 group-hover:opacity-100 transition-opacity remove-file-btn" data-file-name="${fileName}">
                                <i class="fas fa-times-circle"></i>
                            </button>
                        `;
                    }
                    previewContainer.appendChild(fileDiv);
                }
            }
            fileInput.files = currentFiles.files; // 유효한 파일만 다시 설정

            // 동적으로 추가된 제거 버튼에 이벤트 리스너 추가
            previewContainer.querySelectorAll('.remove-file-btn').forEach(button => {
                button.addEventListener('click', function() {
                    const fileNameToRemove = this.dataset.fileName;
                    const dt = new DataTransfer();
                    const files = fileInput.files;

                    for (let j = 0; j < files.length; j++) {
                        if (files.item(j).name !== fileNameToRemove) {
                            dt.items.add(files.item(j));
                        }
                    }
                    fileInput.files = dt.files; // 파일 입력 필드 업데이트
                    this.closest('div').remove(); // 미리보기 div 제거
                });
            });
        });
    }


    // --- 외주 프로젝트 신청 폼 (outsourcingApplicationForm) ---
    const outsourcingApplicationForm = document.getElementById('outsourcingApplicationForm');
    if (outsourcingApplicationForm) {
        outsourcingApplicationForm.addEventListener('submit', function (e) {
            e.preventDefault(); // 기본 폼 제출 방지

            // TODO: 실제 백엔드 연동 시 여기에 AJAX 요청 로직을 추가해야 합니다.
            // 현재는 콘솔 로그와 알림으로 대체합니다.

            const outsourcingId = document.getElementById('outsourcing_id') ? document.getElementById('outsourcing_id').value : 'N/A'; // HTML에서 ID 확인 후 사용
            const proposalDescription = document.getElementById('proposal_description').value.trim();
            const desiredWorkStartDate = document.getElementById('desired_work_start_date').value;
            const desiredWorkEndDate = document.getElementById('desired_work_end_date').value;
            const proposedAmount = document.getElementById('proposed_amount').value;
            const files = document.getElementById('application_reference_files').files;

            // 기본 유효성 검사
            if (!proposalDescription) {
                alert('제안 상세 설명을 입력해주세요.');
                document.getElementById('proposal_description').focus();
                return;
            }
            if (!proposedAmount || parseFloat(proposedAmount) <= 0) {
                alert('제안 금액을 올바르게 입력해주세요.');
                document.getElementById('proposed_amount').focus();
                return;
            }

            console.log('외주 프로젝트 신청 데이터:', {
                outsourcing_id: outsourcingId,
                proposal_description: proposalDescription,
                desired_work_start_date: desiredWorkStartDate,
                desired_work_end_date: desiredWorkEndDate,
                proposed_amount: parseFloat(proposedAmount),
                reference_files_count: files.length
            });
            alert('외주 프로젝트 신청이 성공적으로 접수되었습니다! (실제 저장 로직은 백엔드 구현 필요)');
            
            outsourcingApplicationForm.reset(); // 폼 초기화
            document.getElementById('application-file-preview-container').innerHTML = ''; // 파일 미리보기 초기화
        });

        // 외주 신청 폼의 파일 미리보기 초기화
        initializeFilePreview('application_reference_files', 'application-file-preview-container');
    }

    // --- 포트폴리오 문의 폼 (portfolioInquiryForm) ---
    const portfolioInquiryForm = document.getElementById('portfolioInquiryForm');
    if (portfolioInquiryForm) {
        portfolioInquiryForm.addEventListener('submit', function (e) {
            e.preventDefault(); // 기본 폼 제출 방지

            // TODO: 실제 백엔드 연동 시 여기에 AJAX 요청 로직을 추가해야 합니다.
            // 현재는 콘솔 로그와 알림으로 대체합니다.

            const portfolioId = document.getElementById('portfolio_id').value;
            const inquiryDescription = document.getElementById('inquiry_description').value.trim();
            const desiredProjectStartDate = document.getElementById('desired_project_start_date').value;
            const desiredProjectEndDate = document.getElementById('desired_project_end_date').value;
            const desiredBudget = document.getElementById('desired_budget').value;
            const files = document.getElementById('inquiry_reference_files').files;

            // 기본 유효성 검사
            if (!inquiryDescription) {
                alert('의뢰 상세 내용을 입력해주세요.');
                document.getElementById('inquiry_description').focus();
                return;
            }
            if (desiredBudget && parseFloat(desiredBudget) < 0) {
                alert('희망 예산을 올바르게 입력해주세요.');
                document.getElementById('desired_budget').focus();
                return;
            }

            console.log('포트폴리오 문의 데이터:', {
                portfolio_id: portfolioId,
                inquiry_description: inquiryDescription,
                desired_project_start_date: desiredProjectStartDate,
                desired_project_end_date: desiredProjectEndDate,
                desired_budget: desiredBudget ? parseFloat(desiredBudget) : null,
                reference_files_count: files.length
            });
            alert('포트폴리오 문의 (새 외주 의뢰)가 성공적으로 접수되었습니다! (실제 저장 로직은 백엔드 구현 필요)');
            
            portfolioInquiryForm.reset(); // 폼 초기화
            document.getElementById('inquiry-file-preview-container').innerHTML = ''; // 파일 미리보기 초기화
        });

        // 포트폴리오 문의 폼의 파일 미리보기 초기화
        initializeFilePreview('inquiry_reference_files', 'inquiry-file-preview-container');
    }
});