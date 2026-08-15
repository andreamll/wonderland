const progressBar = document.getElementById("progressBar");
    const processingCard = document.getElementById("processingCard");
    const decisionCard = document.getElementById("decisionCard");
    const referenceCard = document.getElementById("referenceCard");
    const statusChip = document.getElementById("statusChip");
    const decisionTitle = document.getElementById("decisionTitle");
    const formalCopy = document.getElementById("formalCopy");
    const officialReason = document.getElementById("officialReason");
    const displayReference = document.getElementById("displayReference");
    const displayRealId = document.getElementById("displayRealId");
    const loadingStatus = document.getElementById("loadingStatus");
    const loadingCopy = document.getElementById("loadingCopy");

    const whimsicalLoadingMessages = [
      "The White Rabbit is checking whether your timing was acceptable.",
      "The Mad Hatter is reviewing your paperwork between tea refills.",
      "The Cheshire Office is ensuring your documents are both complete and properly mysterious.",
      "A very serious official is consulting a deeply unserious Wonderland handbook."
    ];

    function buildWonderlandReference(realId) {
      const randomChunk = Math.random().toString(36).slice(2, 6).toUpperCase();
      const yearChunk = new Date().getFullYear();
      const paddedRealId = String(realId).padStart(4, "0");
      return `WDL-${yearChunk}-${randomChunk}-${paddedRealId}`;
    }

    async function loadDecision() {
      const params = new URLSearchParams(window.location.search);
      const id = params.get("id");

      if (!id) {
        loadingStatus.textContent = "Reference missing";
        loadingCopy.textContent = "No application ID was provided, so the Ministry cannot produce a decision.";
        return;
      }

      progressBar.style.width = "100%";
      loadingCopy.textContent = whimsicalLoadingMessages[Math.floor(Math.random() * whimsicalLoadingMessages.length)];

      await new Promise((resolve) => setTimeout(resolve, 5000));

      try {
        const response = await fetch(`http://localhost:8080/v1/api/applications/${id}`);

        if (!response.ok) {
          throw new Error("Unable to retrieve application result.");
        }

        const data = await response.json();
        const fancyReference = buildWonderlandReference(data.id);

        processingCard.style.display = "none";
        decisionCard.classList.add("visible");
        referenceCard.style.display = "grid";

        displayReference.textContent = fancyReference;
        displayRealId.textContent = `Application ID: ${data.id}`;

        if (data.decision === "APPROVED") {
          statusChip.textContent = "APPROVED";
          statusChip.classList.add("approved");
          decisionTitle.textContent = "Approved for Entry into Wonderland";
          formalCopy.textContent = "We are pleased to inform you that, following formal review by the Wonderland Ministry of Entry, your application has been approved.";
          officialReason.textContent = data.decisionMessage;
        } else {
          statusChip.textContent = "REJECTED";
          statusChip.classList.add("rejected");
          decisionTitle.textContent = "Application Refused by the Ministry";
          formalCopy.textContent = "We regret to inform you that, after careful review by the appropriate authorities of Wonderland, your application has not been approved at this time.";
          officialReason.textContent = data.decisionMessage;
        }

      } catch (error) {
        processingCard.style.display = "none";
        decisionCard.classList.add("visible");
        statusChip.textContent = "ERROR";
        statusChip.classList.add("rejected");
        decisionTitle.textContent = "The Ministry Could Not Produce a Decision";
        formalCopy.textContent = "We regret to inform you that the Wonderland records office was unable to retrieve your application result.";
        officialReason.textContent = "Please try again later, or return to the portal and submit a fresh application once the Cheshire clerks have reorganized themselves.";
        console.error(error);
      }
    }

    loadDecision();