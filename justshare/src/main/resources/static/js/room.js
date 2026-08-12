
/*
 * ============================================================
 * JUSTSHARE - ROOM.JS
 * ============================================================
 *
 * Handles:
 *
 * 1. Room information
 * 2. Copy room code
 * 3. Copy share URL
 * 4. WebSocket connection
 * 5. Real-time text sharing
 * 6. Loading existing texts
 * 7. Real-time file notifications
 * 8. Loading existing files
 * 9. File upload
 * 10. Upload progress
 * 11. Drag & Drop upload
 * 12. File download
 *
 * NO DELETE FEATURE
 * NO PASSWORD FEATURE
 *
 * ============================================================
 */


/* ============================================================
   GLOBAL
   ============================================================ */

let stompClient = null;


/* ============================================================
   GET ROOM CODE
   ============================================================ */

const params = new URLSearchParams(
    window.location.search
);

const roomCode = params.get("code");

console.log("Room code:", roomCode);


/* ============================================================
   VALIDATE ROOM CODE
   ============================================================ */

if (!roomCode) {

    console.error("Room code is missing.");

    window.location.href = "/";
}


/* ============================================================
   DOM ELEMENTS
   ============================================================ */

const roomCodeElement =
    document.getElementById("roomCode");

const largeRoomCode =
    document.getElementById("largeRoomCode");

const shareUrl =
    document.getElementById("shareUrl");

const createdAt =
    document.getElementById("createdAt");

const expiresAt =
    document.getElementById("expiresAt");

const copyBtn =
    document.getElementById("copyBtn");

const shareBtn =
    document.getElementById("shareBtn");

const textInput =
    document.getElementById("textInput");

const shareTextBtn =
    document.getElementById("shareTextBtn");

const textList =
    document.getElementById("textList");


/* ============================================================
   FILE DOM ELEMENTS
   ============================================================ */

const fileList =
    document.getElementById("fileList");

const dropZone =
    document.getElementById("dropZone");

const fileInput =
    document.getElementById("fileInput");

const uploadStatus =
    document.getElementById("uploadStatus");


/* ============================================================
   DISPLAY ROOM CODE
   ============================================================ */

if (roomCodeElement) {
    roomCodeElement.textContent = roomCode;
}

if (largeRoomCode) {
    largeRoomCode.textContent = roomCode;
}


/* ============================================================
   SHARE URL
   ============================================================ */

const shareUrlValue =
    window.location.origin +
    "/room.html?code=" +
    roomCode;

if (shareUrl) {
    shareUrl.value = shareUrlValue;
}


/* ============================================================
   FORMAT DATE
   ============================================================ */

function formatDate(date) {

    if (!date) {
        return "N/A";
    }

    return new Date(date).toLocaleString();
}


/* ============================================================
   FORMAT FILE SIZE
   ============================================================ */

function formatFileSize(bytes) {

    if (
        bytes === null ||
        bytes === undefined ||
        bytes === 0
    ) {
        return "0 Bytes";
    }

    const units = [
        "Bytes",
        "KB",
        "MB",
        "GB",
        "TB"
    ];

    const index = Math.floor(
        Math.log(bytes) / Math.log(1024)
    );

    const safeIndex = Math.min(
        index,
        units.length - 1
    );

    return (
        (
            bytes /
            Math.pow(1024, safeIndex)
        ).toFixed(2)
        +
        " " +
        units[safeIndex]
    );
}


/* ============================================================
   ESCAPE HTML
   ============================================================ */

function escapeHtml(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* ============================================================
   LOAD ROOM
   ============================================================ */

async function loadRoom() {

    try {

        const response =
            await fetch(
                "/api/rooms/" + roomCode
            );

        if (!response.ok) {

            alert("Room does not exist.");

            window.location.href = "/";

            return;
        }

        const room =
            await response.json();

        if (createdAt) {

            createdAt.textContent =
                "Created: " +
                formatDate(room.createdAt);
        }

        if (expiresAt) {

            expiresAt.textContent =
                "Expires: " +
                formatDate(room.expiresAt);
        }

    } catch (error) {

        console.error(
            "Error loading room:",
            error
        );
    }
}


/* ============================================================
   COPY ROOM CODE
   ============================================================ */

if (copyBtn) {

    copyBtn.addEventListener(
        "click",
        async function () {

            try {

                await navigator.clipboard.writeText(
                    roomCode
                );

                copyBtn.textContent =
                    "Copied!";

                setTimeout(
                    function () {

                        copyBtn.textContent =
                            "Copy";

                    },
                    1500
                );

            } catch (error) {

                console.error(
                    "Copy error:",
                    error
                );

                alert(
                    "Unable to copy room code."
                );
            }
        }
    );
}


/* ============================================================
   COPY SHARE URL
   ============================================================ */

if (shareBtn) {

    shareBtn.addEventListener(
        "click",
        async function () {

            try {

                await navigator.clipboard.writeText(
                    shareUrlValue
                );

                shareBtn.textContent =
                    "Copied!";

                setTimeout(
                    function () {

                        shareBtn.textContent =
                            "Copy";

                    },
                    1500
                );

            } catch (error) {

                console.error(
                    "Copy error:",
                    error
                );

                alert(
                    "Unable to copy URL."
                );
            }
        }
    );
}


/* ============================================================
   WEBSOCKET CONNECTION
   ============================================================ */

function connectWebSocket() {

    console.log(
        "Connecting WebSocket..."
    );

    if (
        typeof StompJs ===
        "undefined"
    ) {

        console.error(
            "StompJs is not loaded."
        );

        return;
    }

    const socketProtocol =
        window.location.protocol === "https:"
            ? "wss://"
            : "ws://";

    const socketUrl =
        socketProtocol +
        window.location.host +
        "/ws";

    console.log(
        "WebSocket URL:",
        socketUrl
    );

    stompClient =
        new StompJs.Client({

            brokerURL: socketUrl,

            reconnectDelay: 5000,

            debug: function (message) {

                console.log(
                    "STOMP:",
                    message
                );
            },

            onConnect: function () {

                console.log(
                    "WebSocket connected!"
                );


                /* ====================================================
                   TEXT SUBSCRIPTION
                   ==================================================== */

                stompClient.subscribe(

                    "/topic/room/" +
                    roomCode,

                    function (message) {

                        try {

                            const text =
                                JSON.parse(
                                    message.body
                                );

                            addTextToUI(
                                text,
                                true
                            );

                        } catch (error) {

                            console.error(
                                "Invalid text message:",
                                error
                            );
                        }
                    }
                );


                /* ====================================================
                   FILE SUBSCRIPTION
                   ==================================================== */

                stompClient.subscribe(

                    "/topic/room/" +
                    roomCode +
                    "/files",

                    function (message) {

                        try {

                            const file =
                                JSON.parse(
                                    message.body
                                );

                            addFileToUI(
                                file,
                                true
                            );

                        } catch (error) {

                            console.error(
                                "Invalid file message:",
                                error
                            );
                        }
                    }
                );


                console.log(
                    "Room subscriptions created."
                );
            },

            onStompError: function (frame) {

                console.error(
                    "STOMP error:",
                    frame
                );
            },

            onWebSocketError: function (error) {

                console.error(
                    "WebSocket error:",
                    error
                );
            },

            onWebSocketClose: function () {

                console.warn(
                    "WebSocket closed."
                );
            }
        });

    stompClient.activate();
}


/* ============================================================
   SHARE TEXT
   ============================================================ */

function shareText() {

    if (!textInput) {

        console.error(
            "textInput element not found."
        );

        return;
    }

    const content =
        textInput.value.trim();

    if (!content) {

        alert(
            "Please enter some text."
        );

        return;
    }

    if (
        !stompClient ||
        !stompClient.connected
    ) {

        alert(
            "WebSocket is not connected."
        );

        return;
    }

    stompClient.publish({

        destination: "/app/text",

        body: JSON.stringify({

            roomCode: roomCode,

            content: content
        })
    });

    textInput.value = "";
}


/* ============================================================
   SHARE TEXT BUTTON
   ============================================================ */

if (shareTextBtn) {

    shareTextBtn.addEventListener(
        "click",
        shareText
    );
}


/* ============================================================
   LOAD EXISTING TEXTS
   ============================================================ */

async function loadTexts() {

    try {

        const response =
            await fetch(
                "/api/rooms/" +
                roomCode +
                "/texts"
            );

        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status
            );
        }

        const texts =
            await response.json();

        renderTexts(texts);

    } catch (error) {

        console.error(
            "Error loading texts:",
            error
        );
    }
}


/* ============================================================
   RENDER TEXTS
   ============================================================ */

function renderTexts(texts) {

    if (!textList) {
        return;
    }

    textList.innerHTML = "";

    if (
        !texts ||
        texts.length === 0
    ) {

        const empty =
            document.createElement("div");

        empty.className =
            "empty-texts";

        empty.textContent =
            "No text shared yet.";

        textList.appendChild(empty);

        return;
    }

    texts.forEach(
        function (text) {

            addTextToUI(
                text,
                false
            );
        }
    );
}


/* ============================================================
   ADD TEXT TO UI
   ============================================================ */

function addTextToUI(
    text,
    prepend = true
) {

    if (!textList) {
        return;
    }

    /*
     * Prevent duplicate cards.
     */

    if (text.id !== undefined) {

        const existing =
            textList.querySelector(
                '[data-text-id="' +
                text.id +
                '"]'
            );

        if (existing) {
            return;
        }
    }

    const card =
        document.createElement("div");

    card.className =
        "text-card";

    if (text.id !== undefined) {

        card.dataset.textId =
            text.id;
    }


    /* Content */

    const content =
        document.createElement("div");

    content.className =
        "text-card-content";

    content.textContent =
        text.content || "";


    /* Time */

    const time =
        document.createElement("div");

    time.className =
        "text-card-time";

    time.textContent =
        formatDate(
            text.createdAt
        );


    card.appendChild(content);
    card.appendChild(time);


    if (prepend) {

        textList.prepend(card);

    } else {

        textList.appendChild(card);
    }
}


/* ============================================================
   UPLOAD FILE
   ============================================================ */

function uploadFile(file) {

    if (!file) {
        return;
    }

    console.log(
        "Uploading:",
        file.name
    );

    if (!uploadStatus) {

        console.error(
            "uploadStatus element not found."
        );

        return;
    }


    /*
     * Upload progress UI.
     */

    uploadStatus.innerHTML = `
        <div class="upload-info">
            <span id="uploadFileName"></span>
            <span id="uploadPercentage">0%</span>
        </div>

        <div class="progress-container">
            <div
                id="uploadProgress"
                class="upload-progress"
                style="width: 0%">
            </div>
        </div>
    `;


    const uploadFileName =
        document.getElementById(
            "uploadFileName"
        );

    const progressBar =
        document.getElementById(
            "uploadProgress"
        );

    const percentage =
        document.getElementById(
            "uploadPercentage"
        );


    if (uploadFileName) {

        uploadFileName.textContent =
            file.name;
    }


    const xhr =
        new XMLHttpRequest();

    const uploadUrl =
        "/api/rooms/" +
        roomCode +
        "/files";


    xhr.open(
        "POST",
        uploadUrl,
        true
    );


    /* Upload progress */

    xhr.upload.addEventListener(
        "progress",
        function (event) {

            if (!event.lengthComputable) {
                return;
            }

            const percent =
                Math.round(
                    (
                        event.loaded /
                        event.total
                    ) * 100
                );

            if (progressBar) {

                progressBar.style.width =
                    percent + "%";
            }

            if (percentage) {

                percentage.textContent =
                    percent + "%";
            }
        }
    );


    /* Upload completed */

    xhr.addEventListener(
        "load",
        function () {

            console.log(
                "Upload response:",
                xhr.status
            );

            if (
                xhr.status >= 200 &&
                xhr.status < 300
            ) {

                if (progressBar) {

                    progressBar.style.width =
                        "100%";
                }

                if (percentage) {

                    percentage.textContent =
                        "100%";
                }

                uploadStatus.innerHTML = `
                    <div class="upload-success">
                        ✓ ${escapeHtml(file.name)}
                        uploaded successfully
                    </div>
                `;

            } else {

                console.error(
                    "Upload failed:",
                    xhr.responseText
                );

                uploadStatus.innerHTML = `
                    <div class="upload-error">
                        ✕ Upload failed
                    </div>
                `;
            }
        }
    );


    /* Network error */

    xhr.addEventListener(
        "error",
        function () {

            console.error(
                "Upload network error"
            );

            uploadStatus.innerHTML = `
                <div class="upload-error">
                    ✕ Network error while uploading
                </div>
            `;
        }
    );


    /* Upload cancelled */

    xhr.addEventListener(
        "abort",
        function () {

            uploadStatus.innerHTML = `
                <div class="upload-error">
                    ✕ Upload cancelled
                </div>
            `;
        }
    );


    /* FormData */

    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );


    /* Start upload */

    xhr.send(formData);
}


/* ============================================================
   FILE INPUT
   ============================================================ */

if (fileInput) {

    fileInput.addEventListener(
        "change",
        function () {

            const file =
                fileInput.files[0];

            uploadFile(file);

            /*
             * Allow selecting the same
             * file again later.
             */

            fileInput.value = "";
        }
    );
}


/* ============================================================
   DRAG OVER
   ============================================================ */

if (dropZone) {

    dropZone.addEventListener(
        "dragover",
        function (event) {

            event.preventDefault();

            dropZone.classList.add(
                "dragover"
            );
        }
    );
}


/* ============================================================
   DRAG LEAVE
   ============================================================ */

if (dropZone) {

    dropZone.addEventListener(
        "dragleave",
        function () {

            dropZone.classList.remove(
                "dragover"
            );
        }
    );
}


/* ============================================================
   DROP FILE
   ============================================================ */

if (dropZone) {

    dropZone.addEventListener(
        "drop",
        function (event) {

            event.preventDefault();

            dropZone.classList.remove(
                "dragover"
            );

            const files =
                event.dataTransfer.files;

            if (
                !files ||
                files.length === 0
            ) {
                return;
            }

            /*
             * Upload first file.
             */

            uploadFile(files[0]);
        }
    );
}


/* ============================================================
   LOAD EXISTING FILES
   ============================================================ */

async function loadFiles() {

    if (!fileList) {

        console.warn(
            "fileList element not found."
        );

        return;
    }

    try {

        const response =
            await fetch(
                "/api/rooms/" +
                roomCode +
                "/files"
            );

        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status
            );
        }

        const files =
            await response.json();

        renderFiles(files);

    } catch (error) {

        console.error(
            "Error loading files:",
            error
        );
    }
}


/* ============================================================
   RENDER FILES
   ============================================================ */

function renderFiles(files) {

    if (!fileList) {
        return;
    }

    fileList.innerHTML = "";

    if (
        !files ||
        files.length === 0
    ) {

        const empty =
            document.createElement("div");

        empty.className =
            "empty-files";

        empty.textContent =
            "No files shared yet.";

        fileList.appendChild(empty);

        return;
    }

    files.forEach(
        function (file) {

            addFileToUI(
                file,
                false
            );
        }
    );
}


/* ============================================================
   ADD FILE TO UI
   ============================================================ */

function addFileToUI(
    file,
    prepend = true
) {

    if (!fileList) {
        return;
    }


    /*
     * File ID.
     */

    const fileId =
        file.id ||
        file.fileId;


    if (
        fileId === undefined ||
        fileId === null
    ) {

        console.warn(
            "File ID missing:",
            file
        );

        return;
    }


    /*
     * Prevent duplicates.
     */

    const existing =
        fileList.querySelector(
            '[data-file-id="' +
            fileId +
            '"]'
        );

    if (existing) {
        return;
    }


    /*
     * Remove empty state.
     */

    const emptyMessage =
        fileList.querySelector(
            ".empty-files"
        );

    if (emptyMessage) {
        emptyMessage.remove();
    }


    /*
     * Create card.
     */

    const card =
        document.createElement("div");

    card.className =
        "file-card";

    card.dataset.fileId =
        fileId;


    /*
     * File information.
     */

    const info =
        document.createElement("div");

    info.className =
        "file-info";


    const name =
        document.createElement("strong");

    name.textContent =
        file.originalName ||
        file.fileName ||
        file.name ||
        "Unknown file";


    const size =
        document.createElement("span");

    const fileSize =
        file.fileSize ||
        file.size ||
        0;

    size.textContent =
        formatFileSize(fileSize);


    info.appendChild(name);
    info.appendChild(size);


    /*
     * Actions.
     */

    const actions =
        document.createElement("div");

    actions.className =
        "file-actions";


    /*
     * Download button.
     *
     * Everyone can download.
     */

    const downloadButton =
        document.createElement("a");

    downloadButton.className =
        "download-btn";

    downloadButton.textContent =
        "Download";

    downloadButton.href =
        "/api/rooms/" +
        roomCode +
        "/files/" +
        fileId +
        "/download";

    downloadButton.setAttribute(
        "download",
        ""
    );

    actions.appendChild(
        downloadButton
    );


    /*
     * Build card.
     */

    card.appendChild(info);
    card.appendChild(actions);


    /*
     * Add to list.
     */

    if (prepend) {

        fileList.prepend(card);

    } else {

        fileList.appendChild(card);
    }
}


/* ============================================================
   INITIALIZE PAGE
   ============================================================ */

loadRoom();

loadTexts();

loadFiles();

connectWebSocket();


console.log(
    "room.js initialized successfully."
);

