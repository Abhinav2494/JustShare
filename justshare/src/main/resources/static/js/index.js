const createRoomBtn =
    document.getElementById("createRoomBtn");

const joinRoomBtn =
    document.getElementById("joinRoomBtn");

const roomCodeInput =
    document.getElementById("roomCodeInput");

const errorMessage =
    document.getElementById("errorMessage");


/*
    CREATE ROOM
*/

createRoomBtn.addEventListener("click", async () => {

    try {

        createRoomBtn.disabled = true;

        createRoomBtn.textContent = "Creating...";


        const response = await fetch("/api/rooms", {
            method: "POST"
        });


        if (!response.ok) {
            throw new Error("Unable to create room");
        }


        const room = await response.json();


        /*
            Example:

            room.roomCode = A7K29P
        */

        window.location.href =
            `/room.html?code=${room.roomCode}`;

    }

    catch (error) {

        console.error(error);

        errorMessage.textContent =
            "Unable to create room. Please try again.";

        createRoomBtn.disabled = false;

        createRoomBtn.textContent =
            "Create Room";
    }

});


/*
    JOIN ROOM
*/

joinRoomBtn.addEventListener("click", async () => {

    const code =
        roomCodeInput.value.trim().toUpperCase();


    if (!code) {

        errorMessage.textContent =
            "Please enter a room code.";

        return;
    }


    try {

        joinRoomBtn.disabled = true;

        joinRoomBtn.textContent = "Checking...";


        const response =
            await fetch(`/api/rooms/${code}`);


        if (!response.ok) {

            errorMessage.textContent =
                "Room not found.";

            return;
        }


        const room =
            await response.json();


        window.location.href =
            `/room.html?code=${room.roomCode}`;

    }

    catch (error) {

        console.error(error);

        errorMessage.textContent =
            "Something went wrong.";

    }

    finally {

        joinRoomBtn.disabled = false;

        joinRoomBtn.textContent = "Join";

    }

});